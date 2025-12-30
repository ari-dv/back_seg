package Proyecto.MegaWeb2.__BackEnd.Service;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class TwoFactorService {

    private final GoogleAuthenticator gAuth = new GoogleAuthenticator();

    // 🔹 Para manejo de códigos temporales de 2FA vía correo
    private final Map<String, CodigoTemporal> codigosCorreo = new HashMap<>();
    private final Random random = new Random();

    // ===================== GOOGLE AUTHENTICATOR =====================
    // 🔹 Genera un secreto único para el usuario
    public String generateSecret() {
        GoogleAuthenticatorKey key = gAuth.createCredentials();
        return key.getKey();
    }

    // 🔹 Genera la URL compatible con Google Authenticator
    public String getOtpAuthURL(String username, String secret) {
        String issuer = "MegaWeb"; // Nombre de tu app
        return String.format(
                "otpauth://totp/%s:%s?secret=%s&issuer=%s",
                issuer, username, secret, issuer
        );
    }

    // 🔹 Verifica si el código ingresado por el usuario es válido
    public boolean verifyCode(String secret, int code) {
        return gAuth.authorize(secret, code);
    }

    // ===================== CÓDIGO 6 DÍGITOS POR CORREO =====================
    /**
     * Genera un código de 6 dígitos y lo guarda temporalmente asociado al email.
     */
    public String generarCodigo6Digitos(String email) {
        int numero = 100000 + random.nextInt(900000); // 6 dígitos
        String codigo = String.valueOf(numero);

        // Guardar con tiempo de expiración (5 minutos)
        codigosCorreo.put(email, new CodigoTemporal(codigo, LocalDateTime.now().plusMinutes(5)));

        return codigo;
    }

    /**
     * Valida el código ingresado por el usuario vía correo
     */
    public boolean validarCodigo(String email, String codigoIngresado) {
        CodigoTemporal ct = codigosCorreo.get(email);
        if (ct == null) return false;
        if (ct.getFechaExpiracion().isBefore(LocalDateTime.now())) {
            codigosCorreo.remove(email);
            return false;
        }
        boolean valido = ct.getCodigo().equals(codigoIngresado);
        if (valido) codigosCorreo.remove(email); // solo usar una vez
        return valido;
    }

    // ===================== CLASE INTERNA =====================
    private static class CodigoTemporal {
        private final String codigo;
        private final LocalDateTime fechaExpiracion;

        public CodigoTemporal(String codigo, LocalDateTime fechaExpiracion) {
            this.codigo = codigo;
            this.fechaExpiracion = fechaExpiracion;
        }

        public String getCodigo() {
            return codigo;
        }

        public LocalDateTime getFechaExpiracion() {
            return fechaExpiracion;
        }
    }
}
