package com.gateway.redireccion.productos;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api/proxy/productos")
@RequiredArgsConstructor
public class ProductoProxyController {

    private final RestTemplate restTemplate;

    @RequestMapping(value = "/**", method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
    public ResponseEntity<?> proxyProductos(HttpServletRequest request,
                                            @RequestBody(required = false) String body,
                                            @RequestHeader HttpHeaders headers) {

        // Construir ruta final
        String originalPath = request.getRequestURI().replace("/api/proxy/productos", "");
        String targetUrl = "http://localhost:8083/api/v1/productos" + originalPath;

        HttpMethod method = HttpMethod.valueOf(request.getMethod());

        // Clonar headers y limpiar los conflictivos
        HttpHeaders cleanHeaders = new HttpHeaders();
        headers.forEach((key, value) -> {
            if (!key.equalsIgnoreCase(HttpHeaders.CONTENT_LENGTH)) {
                cleanHeaders.put(key, value);
            }
        });
        cleanHeaders.setContentType(MediaType.APPLICATION_JSON); // importante

        // Crear la entidad a enviar
        HttpEntity<String> entity = new HttpEntity<>(body, cleanHeaders);

        // Redirigir al microservicio
        ResponseEntity<String> response = restTemplate.exchange(targetUrl, method, entity, String.class);

        return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
    }
    
}
