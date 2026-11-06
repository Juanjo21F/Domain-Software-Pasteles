package com.pasteleria.facturacion.infrastructure.adapters.in.rest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

/** Prueba de punta a punta (HTTP -> casos de uso -> dominio -> H2) con seguridad JWT real. */
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = "seguridad.jwt.secret-base64=Lu7e1SCfczRWZpilqgAQVZqNiJopaxOzxiu9UaLENKGTeGeBtIyfpBegmsIKQS3D6pmEiahNszQXjzxmhjabaw==")
class FacturacionRestTest {

    private static final String SECRETO = "Lu7e1SCfczRWZpilqgAQVZqNiJopaxOzxiu9UaLENKGTeGeBtIyfpBegmsIKQS3D6pmEiahNszQXjzxmhjabaw==";

    @Autowired
    private MockMvc mvc;

    private String token(String... roles) {
        Instant ahora = Instant.now();
        return Jwts.builder().subject("tester").claim("uid", 99).claim("roles", List.of(roles))
                .issuedAt(Date.from(ahora)).expiration(Date.from(ahora.plus(Duration.ofHours(1))))
                .signWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRETO)), Jwts.SIG.HS384).compact();
    }

    private MockHttpServletRequestBuilder conToken(MockHttpServletRequestBuilder peticion, String token, String json) {
        MockHttpServletRequestBuilder b = peticion.header("Authorization", "Bearer " + token);
        return json == null ? b : b.contentType(MediaType.APPLICATION_JSON).content(json);
    }

    @Test
    @DisplayName("Sin token o con token falso: 401")
    void sinCredencialesEs401() throws Exception {
        mvc.perform(get("/api/productos")).andExpect(status().isUnauthorized());
        mvc.perform(conToken(get("/api/productos"), "token.falso.aqui", null)).andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Un USER puede consultar pero no administrar el catálogo ni ver reportes: 403")
    void usuarioNormalNoAdministra() throws Exception {
        String user = token("USER");
        mvc.perform(conToken(get("/api/productos"), user, null)).andExpect(status().isOk());
        mvc.perform(conToken(post("/api/productos"), user,
                "{\"codigo\":\"X-001\",\"nombre\":\"X\",\"categoria\":\"C\",\"precio\":1000,\"stock\":1}"))
                .andExpect(status().isForbidden());
        LocalDate hoy = LocalDate.now();
        mvc.perform(conToken(get("/api/reportes/ventas-mensuales?anio=" + hoy.getYear() + "&mes=" + hoy.getMonthValue()),
                user, null)).andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Flujo HU-8 a HU-12: producto, cliente, factura, stock, anulación y reporte")
    void flujoCompleto() throws Exception {
        String admin = token("USER", "ADMIN");

        mvc.perform(conToken(post("/api/productos"), admin,
                "{\"codigo\":\"TOR-900\",\"nombre\":\"Torta de prueba\",\"categoria\":\"Tortas\",\"precio\":45000,\"stock\":10}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.codigo").value("TOR-900"))
                .andExpect(jsonPath("$.stock").value(10));

        mvc.perform(conToken(post("/api/productos"), admin,
                "{\"codigo\":\"TOR-900\",\"nombre\":\"Repetida\",\"categoria\":\"Tortas\",\"precio\":1,\"stock\":1}"))
                .andExpect(status().isConflict());

        mvc.perform(conToken(post("/api/clientes"), admin,
                "{\"documento\":\"1090999001\",\"nombre\":\"Cliente Prueba\",\"telefono\":\"3001112233\",\"correo\":\"c@p.com\"}"))
                .andExpect(status().isCreated());

        String factura = "{\"clienteDocumento\":\"1090999001\",\"lineas\":[{\"codigoProducto\":\"TOR-900\",\"cantidad\":3}]}";
        String numero = com.jayway.jsonpath.JsonPath.read(
                mvc.perform(conToken(post("/api/facturas"), admin, factura))
                        .andExpect(status().isCreated())
                        .andExpect(jsonPath("$.total").value(135000.0))
                        .andExpect(jsonPath("$.estado").value("EMITIDA"))
                        .andReturn().getResponse().getContentAsString(), "$.numero");

        mvc.perform(conToken(get("/api/productos/TOR-900"), admin, null))
                .andExpect(jsonPath("$.stock").value(7));

        LocalDate hoy = LocalDate.now();
        mvc.perform(conToken(get("/api/reportes/ventas-mensuales?anio=" + hoy.getYear() + "&mes=" + hoy.getMonthValue()),
                admin, null))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productos[?(@.codigo=='TOR-900')].cantidadVendida").value(3));

        mvc.perform(conToken(post("/api/facturas/" + numero + "/anulacion"), admin, null))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("ANULADA"));
        mvc.perform(conToken(get("/api/productos/TOR-900"), admin, null))
                .andExpect(jsonPath("$.stock").value(10));
        mvc.perform(conToken(post("/api/facturas/" + numero + "/anulacion"), admin, null))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("HU-11: sin stock suficiente responde 409, detalla el faltante y no cambia el stock")
    void ventaSinStock() throws Exception {
        String admin = token("ADMIN");
        mvc.perform(conToken(post("/api/productos"), admin,
                "{\"codigo\":\"CUP-900\",\"nombre\":\"Cupcake\",\"categoria\":\"Cupcakes\",\"precio\":6500,\"stock\":2}"))
                .andExpect(status().isCreated());
        mvc.perform(conToken(post("/api/clientes"), admin, "{\"documento\":\"1090999002\",\"nombre\":\"Otro Cliente\"}"))
                .andExpect(status().isCreated());

        mvc.perform(conToken(post("/api/facturas"), admin,
                "{\"clienteDocumento\":\"1090999002\",\"lineas\":[{\"codigoProducto\":\"CUP-900\",\"cantidad\":5}]}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.detalles[0]").value(org.hamcrest.Matchers.containsString("CUP-900")));

        mvc.perform(conToken(get("/api/productos/CUP-900"), admin, null)).andExpect(jsonPath("$.stock").value(2));
    }

    @Test
    void recursosInexistentesDan404YDatosInvalidosDan400() throws Exception {
        String admin = token("ADMIN");
        mvc.perform(conToken(get("/api/productos/NOEXISTE"), admin, null)).andExpect(status().isNotFound());
        mvc.perform(conToken(post("/api/productos"), admin,
                "{\"codigo\":\"BAD-1\",\"nombre\":\"Malo\",\"categoria\":\"C\",\"precio\":-5,\"stock\":1}"))
                .andExpect(status().isBadRequest());
        mvc.perform(conToken(post("/api/productos"), admin, "esto no es json")).andExpect(status().isBadRequest());
        mvc.perform(conToken(patch("/api/productos/NOEXISTE/desactivar"), admin, null)).andExpect(status().isNotFound());
    }
}
