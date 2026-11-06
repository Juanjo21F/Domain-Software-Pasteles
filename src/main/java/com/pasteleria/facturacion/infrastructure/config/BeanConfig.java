package com.pasteleria.facturacion.infrastructure.config;

import com.pasteleria.facturacion.application.port.out.ClienteRepositoryPort;
import com.pasteleria.facturacion.application.port.out.FacturaRepositoryPort;
import com.pasteleria.facturacion.application.port.out.NumeracionFacturaPort;
import com.pasteleria.facturacion.application.port.out.ProductoRepositoryPort;
import com.pasteleria.facturacion.application.port.out.TransaccionPort;
import com.pasteleria.facturacion.application.service.AnularFacturaService;
import com.pasteleria.facturacion.application.service.ClienteService;
import com.pasteleria.facturacion.application.service.ConsultarFacturaService;
import com.pasteleria.facturacion.application.service.ConsultarReporteVentasService;
import com.pasteleria.facturacion.application.service.EmitirFacturaService;
import com.pasteleria.facturacion.application.service.ProductoService;
import com.pasteleria.facturacion.domain.service.EmisionFacturaService;
import com.pasteleria.facturacion.domain.service.ReporteVentasService;
import java.time.Clock;
import java.time.ZoneId;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Raíz de composición. El dominio y la aplicación no llevan anotaciones de Spring (Semana 5, DIP):
 * es esta clase, en el borde, la que los construye y les entrega sus dependencias.
 */
@Configuration
public class BeanConfig {

    @Bean
    Clock reloj(@Value("${negocio.zona-horaria}") String zona) {
        return Clock.system(ZoneId.of(zona));
    }

    @Bean
    EmisionFacturaService emisionFacturaService() {
        return new EmisionFacturaService();
    }

    @Bean
    ReporteVentasService reporteVentasService() {
        return new ReporteVentasService();
    }

    @Bean
    ProductoService productoService(ProductoRepositoryPort productos) {
        return new ProductoService(productos);
    }

    @Bean
    ClienteService clienteService(ClienteRepositoryPort clientes) {
        return new ClienteService(clientes);
    }

    @Bean
    EmitirFacturaService emitirFacturaService(ClienteRepositoryPort clientes, ProductoRepositoryPort productos,
                                              FacturaRepositoryPort facturas, NumeracionFacturaPort numeracion,
                                              TransaccionPort transaccion, Clock reloj, EmisionFacturaService emision) {
        return new EmitirFacturaService(clientes, productos, facturas, numeracion, transaccion, reloj, emision);
    }

    @Bean
    AnularFacturaService anularFacturaService(FacturaRepositoryPort facturas, ProductoRepositoryPort productos,
                                              TransaccionPort transaccion) {
        return new AnularFacturaService(facturas, productos, transaccion);
    }

    @Bean
    ConsultarFacturaService consultarFacturaService(FacturaRepositoryPort facturas) {
        return new ConsultarFacturaService(facturas);
    }

    @Bean
    ConsultarReporteVentasService consultarReporteVentasService(FacturaRepositoryPort facturas,
                                                                ReporteVentasService reporte) {
        return new ConsultarReporteVentasService(facturas, reporte);
    }
}
