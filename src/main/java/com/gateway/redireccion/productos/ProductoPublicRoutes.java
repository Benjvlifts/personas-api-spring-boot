package com.gateway.redireccion.productos;

public class ProductoPublicRoutes {

    public static final String[] PRODUCTOS_PUBLIC_GET = {
        "/api/proxy/productos", // GET todos
        "/api/proxy/productos/{codigo}" // GET por código
    };

}
