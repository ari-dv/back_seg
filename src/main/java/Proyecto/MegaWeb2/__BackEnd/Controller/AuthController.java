package Proyecto.MegaWeb2.__BackEnd.Controller;

import Proyecto.MegaWeb2.__BackEnd.Dto.ResponseLoginDto;
import Proyecto.MegaWeb2.__BackEnd.Security.JwtUtil;
import Proyecto.MegaWeb2.__BackEnd.Service.UsuarioService;
import Proyecto.MegaWeb2.__BackEnd.Repository.UsuarioRepository;
import Proyecto.MegaWeb2.__BackEnd.Dto.UsuarioDTO;
import Proyecto.MegaWeb2.__BackEnd.Service.EmailService;
import Proyecto.MegaWeb2.__BackEnd.Service.HashUtil;
import Proyecto.MegaWeb2.__BackEnd.Service.TwoFactorService;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private TwoFactorService twoFactorService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private EmailService emailService;

    private Map<String, Object> createResponse(String status, String message, Object data) {
        Map<String, Object> map = new HashMap<>();
        map.put("status", status);
        map.put("message", message);
        map.put("data", data);
        return map;
    }

    // ================= LOGIN =================
@PostMapping("/login")
public ResponseEntity<?> login(@RequestBody Map<String, String> body) {

    String email = body.get("email");  
    String password = body.get("password");

    if (email == null || email.isEmpty() || password == null || password.isEmpty()) {
        return ResponseEntity.badRequest()
                .body(createResponse("error", "Faltan datos de login", null));
    }

    try {
        // 🔹 Autenticación
        UsuarioDTO user = usuarioService.authenticateByEmail(email, password);

        if (user == null) {
            // Diferenciamos si el email existe o solo la contraseña está mal
            UsuarioDTO userExist = usuarioService.findByEmail(email);
            if (userExist == null) {
                return ResponseEntity.status(404)
                        .body(createResponse("error", "Correo no registrado", null));
            } else {
                return ResponseEntity.status(401)
                        .body(createResponse("error", "Contraseña incorrecta", null));
            }
        }

        // 🔹 Verificamos si el usuario está activo
        if (user.getEstado() == 0) { // 0 = desactivado, 1 = activo
            return ResponseEntity.status(403)
                    .body(createResponse("error", "Usuario desactivado", null));
        }

        ResponseLoginDto response = new ResponseLoginDto();
        response.setUsername(email);
        response.setRequire2FA(user.isTwoFactorEnabled());

        // 🔹 Si requiere 2FA
        if (user.isTwoFactorEnabled()) {
            return ResponseEntity.ok(
                    createResponse("success", "Se requiere verificación 2FA", response)
            );
        }

        // 🔹 Generar token JWT de forma segura
        Map<String, Object> tokenData = jwtUtil.generateToken(user);
        if (tokenData == null || !tokenData.containsKey("token") || !tokenData.containsKey("expiration")) {
            return ResponseEntity.status(500)
                    .body(createResponse("error", "Error generando token", null));
        }

        response.setToken((String) tokenData.get("token"));
        Object expObj = tokenData.get("expiration");
        long expiration = (expObj instanceof Long) ? (Long) expObj : ((Integer) expObj).longValue();
        response.setExpiration(new Date(expiration));
        response.setIsFirstAuthGoogle(0);

        return ResponseEntity.ok(
                createResponse("success", "Login exitoso", response)
        );

    } catch (Exception e) {
        e.printStackTrace(); // 🔹 Para debug
        return ResponseEntity.status(500)
                .body(createResponse("error", "Error interno del servidor: " + e.getMessage(), null));
    }
}

    // ================= FORGOT PASSWORD =================
    @PostMapping("/forgot-password")
    public ResponseEntity<?> recuperarPassword(@RequestBody Map<String, String> body) {

        String email = body.get("email");

        if (email == null || email.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(createResponse("error", "Falta el correo electrónico", null));
        }

        UsuarioDTO user = usuarioService.findByEmail(email);
        if (user == null) {
            return ResponseEntity.status(404)
                    .body(createResponse("error", "Usuario no encontrado", null));
        }

        String token = jwtUtil.generateTemporaryToken(email, Duration.ofMinutes(15));
        String resetLink = "http://localhost:4200/reset-password?token=" + token;

        String asunto = "🔐 Recupera tu contraseña - MegaWeb";

      String mensajeHtml =
"<!DOCTYPE html>" +
"<html lang='es'>" +
"<head>" +
"<meta charset='UTF-8'>" +
"<meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
"<title>Recuperación de contraseña</title>" +
"</head>" +

"<body style='margin:0;padding:0;background-color:#f4f6f8;font-family:Arial,Helvetica,sans-serif;'>" +

"<table width='100%' cellpadding='0' cellspacing='0' style='padding:30px 10px;'>" +
"<tr><td align='center'>" +

"<table width='600' cellpadding='0' cellspacing='0' style='background:#ffffff;border-radius:14px;" +
"box-shadow:0 8px 24px rgba(0,0,0,0.08);overflow:hidden;'>" +

/* ===== HEADER ===== */
"<tr>" +
"<td style='background:#1e88e5;padding:25px;text-align:center;'>" +
"<h1 style='color:#ffffff;margin:0;font-size:24px;letter-spacing:0.5px;'>MegaWeb</h1>" +
"<p style='color:#e3f2fd;margin:6px 0 0;font-size:14px;'>Seguridad de tu cuenta</p>" +
"</td>" +
"</tr>" +

/* ===== BODY ===== */
"<tr>" +
"<td style='padding:35px 40px;color:#444;font-size:15px;line-height:1.7;'>" +

"<h2 style='margin-top:0;color:#2c3e50;font-size:20px;'>🔐 Recuperación de contraseña</h2>" +

"<p>Hola,</p>" +
"<p>Hemos recibido una solicitud para restablecer la contraseña de tu cuenta en <b>MegaWeb</b>.</p>" +
"<p>Para continuar, haz clic en el botón de abajo:</p>" +

"<div style='text-align:center;margin:35px 0;'>" +
"<a href='" + resetLink + "' " +
"style='background:#1e88e5;color:#ffffff;text-decoration:none;" +
"padding:14px 36px;border-radius:8px;font-weight:bold;" +
"font-size:15px;display:inline-block;" +
"box-shadow:0 6px 16px rgba(30,136,229,0.4);'>" +
"Restablecer contraseña</a>" +
"</div>" +

"<p style='font-size:14px;color:#555;'>Si no solicitaste este cambio, puedes ignorar este correo de forma segura.</p>" +

"<div style='background:#f5f7fa;padding:12px 16px;border-radius:8px;margin-top:20px;'>" +
"<p style='margin:0;font-size:13px;color:#666;'>⏰ <b>Importante:</b> Este enlace expirará en 15 minutos.</p>" +
"</div>" +

"</td>" +
"</tr>" +

/* ===== FOOTER ===== */
"<tr>" +
"<td style='background:#fafafa;padding:20px;text-align:center;border-top:1px solid #eee;'>" +
"<p style='margin:0;font-size:12px;color:#999;'>" +
"© 2025 MegaWeb · Todos los derechos reservados" +
"</p>" +
"<p style='margin:6px 0 0;font-size:12px;color:#bbb;'>" +
"Este es un correo automático, por favor no respondas" +
"</p>" +
"</td>" +
"</tr>" +

"</table>" +
"</td></tr>" +
"</table>" +

"</body></html>";


        emailService.enviarCorreo(email, asunto, mensajeHtml);

        return ResponseEntity.ok(
                createResponse("success", "Correo de recuperación enviado", null)
        );
    }

    // ================= RESET PASSWORD =================
    @PostMapping("/restablecer-password")
    public ResponseEntity<?> restablecerPassword(@RequestBody Map<String, String> body) {

        String token = body.get("token");
        String nuevaPassword = body.get("password");

        if (token == null || nuevaPassword == null) {
            return ResponseEntity.badRequest()
                    .body(createResponse("error", "Datos incompletos", null));
        }

        String email = jwtUtil.validarYObtenerEmail(token);
        if (email == null) {
            return ResponseEntity.status(400)
                    .body(createResponse("error", "Token inválido o expirado", null));
        }

        UsuarioDTO user = usuarioService.findByEmail(email);
        if (user == null) {
            return ResponseEntity.status(404)
                    .body(createResponse("error", "Usuario no encontrado", null));
        }

        usuarioService.updatePassword(email, nuevaPassword);

        return ResponseEntity.ok(
                createResponse("success", "Contraseña actualizada correctamente", null)
        );
    }
}
