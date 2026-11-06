# Domain Service · Pastelería (Grupo 5) — avance

Servicio de dominio de facturación (HU-8 a HU-12) sobre arquitectura hexagonal. Valida el JWT que emite **AuthCore**
(firma compartida, HS384). Aplica lo visto en las semanas 5, 6 y 7 de Ingeniería del Software III.

## Estado (leer primero)

| Capa | Estado |
|---|---|
| `domain` + `application` (Java puro) | Compiladas con `javac` y **68 pruebas ejecutadas, todas en verde** |
| `infrastructure` (Spring, JPA, JWT, REST) | **Escrita pero NO compilada ni ejecutada**: el entorno donde se generó no podía descargar dependencias de Maven. Lo primero es correr `mvn verify` |
| Pruebas con Spring (REST, JPA, ArchUnit, JWT) | Escritas, **no ejecutadas** |

Si `mvn verify` muestra algún error de compilación o de prueba en infraestructura, es de esperar y se corrige rápido;
el núcleo no depende de ella.

## Cómo se ve cada semana en el código

| Semana | Tema | Dónde está |
|---|---|---|
| 5 | **S**RP | Un caso de uso por clase (`EmitirFacturaService`, `AnularFacturaService`…); `JwtTokenValidator` valida y `JwtAuthenticationFilter` solo extrae el token |
| 5 | **O**CP | Nuevos reportes o reglas = clases nuevas; el flujo existente no se toca |
| 5 | **L**SP | `contract/*RepositoryContractTest`: las mismas pruebas pasan el doble en memoria y el adaptador JPA |
| 5 | **I**SP | Casos de uso pequeños en `application/port/in` en vez de una interfaz gigante |
| 5 | **D**IP | `application` solo conoce puertos (`port/out`); `BeanConfig` conecta todo en el borde. ArchUnit lo vigila |
| 6 | Núcleo de dominio | Objetos de valor (`Dinero`, `CodigoProducto`, `Documento`, `NumeroFactura`), entidades (`Producto`, `Cliente`), raíz de agregado `Factura`, servicios de dominio (`EmisionFacturaService`, `ReporteVentasService`) |
| 7 | Factory | `crear()`, `reconstruir()`, `Dinero.de()`, `NumeroFactura.de()` |
| 7 | Builder | `FacturaBuilder` |
| 7 | Adapter / Mapper | `infrastructure/adapters/out/persistence/mapper` (dominio ↔ entidad JPA) |
| 7 | Repository | Puertos `*RepositoryPort` + `*RepositoryAdapter` |

```
domain/          Java puro: vo, model, service, exception
application/     port/in (casos de uso + comandos), port/out (repositorios, numeración, transacción), service
infrastructure/  config (composición), security (JWT), adapters/in/rest, adapters/out/persistence
```

## Endpoints

Todos exigen `Authorization: Bearer <JWT de AuthCore>`.

| HU | Método y ruta | Rol |
|---|---|---|
| 8 | `POST /api/productos` · `PUT /api/productos/{codigo}` · `PATCH /api/productos/{codigo}/desactivar` | ADMIN |
| 8 | `GET /api/productos?soloActivos=` · `GET /api/productos/{codigo}` | autenticado |
| 9 | `POST /api/clientes` · `GET /api/clientes/{documento}` · `PUT /api/clientes/{documento}` | autenticado |
| 10, 11 | `POST /api/facturas` (valida y descuenta stock; todo o nada) · `GET /api/facturas/{numero}` | autenticado |
| — | `POST /api/facturas/{numero}/anulacion` (devuelve el stock) | ADMIN |
| 12 | `GET /api/reportes/ventas-mensuales?anio=&mes=` | ADMIN |

Errores: 400 dato inválido · 401 sin token · 403 sin permiso · 404 no existe · 409 duplicado, sin stock, ya anulada, conflicto de concurrencia.
Hay ejemplos listos en `requests.http`.

## Ejecutar

```bash
export JWT_SECRET_BASE64='<la misma clave Base64 que usa AuthCore>'   # sin esto la app no arranca, a propósito
mvn verify                      # compila y corre todas las pruebas
mvn spring-boot:run             # puerto 8082, H2 en memoria
# producción: SPRING_PROFILES_ACTIVE=prod DB_URL=... DB_USER=... DB_PASSWORD=...
```

La clave debe tener al menos 48 bytes para HS384. La clave nunca va en el repositorio.

## Supuestos de negocio (confirmar con el grupo)

1. Número de factura `FAC-AAAA-NNNNNN`, consecutivo por año, sin huecos si la venta se cancela.
2. Precio y nombre se **congelan** en la línea al facturar; cambiar el producto después no altera facturas viejas.
3. Un producto repetido en varias líneas se suma antes de validar el stock.
4. No se vende un producto desactivado. Anular devuelve el stock; no se puede anular dos veces.
5. El reporte ignora facturas anuladas; orden: más unidades, luego más ingresos, luego código.
6. Roles: catálogo, anulación y reportes solo ADMIN. Es una decisión mía, no viene en el informe.
7. Moneda: pesos colombianos con 2 decimales; zona horaria `America/Bogota`.

## Pendiente

Migraciones Flyway (hoy `ddl-auto: update`), paginación en listados, reintento automático ante conflicto de concurrencia,
pruebas de concurrencia reales sobre el bloqueo optimista, y mapear los roles exactos que emite AuthCore.
