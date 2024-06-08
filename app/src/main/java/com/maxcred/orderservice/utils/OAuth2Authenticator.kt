import com.sun.mail.util.MailSSLSocketFactory
import java.security.GeneralSecurityException
import javax.mail.Authenticator
import javax.mail.PasswordAuthentication
import javax.mail.Session
import javax.mail.Transport

class OAuth2Authenticator : Authenticator() {
    override fun getPasswordAuthentication(): PasswordAuthentication? {
        // Retorna null para indicar que o accessToken deve ser usado
        return null
    }

    companion object {
        fun initialize() {
            // Nenhuma inicialização especial necessária
        }

        @Throws(GeneralSecurityException::class)
        fun connectToSmtp(session: Session, username: String?, refreshToken: String?): Transport {
            // Configurar propriedades adicionais para TLS
            val sf = MailSSLSocketFactory()
            sf.isTrustAllHosts = true

            val props = session.properties
            props["mail.smtp.ssl.enable"] = "true"
            props["mail.smtp.ssl.socketFactory"] = sf

            // Conectar ao servidor SMTP com usuário e senha
            val transport = session.getTransport("smtp")
            transport.connect("smtp.gmail.com", username, refreshToken)
            return transport
        }
    }
}