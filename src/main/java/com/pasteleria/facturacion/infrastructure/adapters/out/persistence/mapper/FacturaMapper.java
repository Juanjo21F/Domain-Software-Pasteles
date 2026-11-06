package com.pasteleria.facturacion.infrastructure.adapters.out.persistence.mapper;

import com.pasteleria.facturacion.domain.model.EstadoFactura;
import com.pasteleria.facturacion.domain.model.Factura;
import com.pasteleria.facturacion.domain.model.LineaFactura;
import com.pasteleria.facturacion.domain.vo.CodigoProducto;
import com.pasteleria.facturacion.domain.vo.Dinero;
import com.pasteleria.facturacion.domain.vo.Documento;
import com.pasteleria.facturacion.domain.vo.NumeroFactura;
import com.pasteleria.facturacion.infrastructure.adapters.out.persistence.entity.FacturaEntity;
import com.pasteleria.facturacion.infrastructure.adapters.out.persistence.entity.FacturaEntity.EstadoFacturaDb;
import com.pasteleria.facturacion.infrastructure.adapters.out.persistence.entity.LineaFacturaEmbeddable;
import java.util.List;

public final class FacturaMapper {

    private FacturaMapper() { }

    public static Factura aDominio(FacturaEntity e) {
        List<LineaFactura> lineas = e.getLineas().stream()
                .map(l -> new LineaFactura(CodigoProducto.de(l.getCodigoProducto()), l.getNombreProducto(),
                        Dinero.de(l.getPrecioUnitario()), l.getCantidad()))
                .toList();
        return Factura.reconstruir(NumeroFactura.desde(e.getNumero()), e.getFechaEmision(),
                Documento.de(e.getClienteDocumento()), lineas, aDominio(e.getEstado()));
    }

    public static FacturaEntity aEntidad(Factura f) {
        List<LineaFacturaEmbeddable> lineas = f.getLineas().stream()
                .map(l -> new LineaFacturaEmbeddable(l.codigoProducto().valor(), l.nombreProducto(),
                        l.precioUnitario().valor(), l.cantidad()))
                .toList();
        return new FacturaEntity(f.getNumero().valor(), f.getFechaEmision(), f.getClienteDocumento().valor(),
                aDb(f.getEstado()), lineas);
    }

    public static EstadoFacturaDb aDb(EstadoFactura estado) {
        return EstadoFacturaDb.valueOf(estado.name());
    }

    private static EstadoFactura aDominio(EstadoFacturaDb estado) {
        return EstadoFactura.valueOf(estado.name());
    }
}
