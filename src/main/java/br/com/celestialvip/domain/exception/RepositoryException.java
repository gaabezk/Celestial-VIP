package br.com.celestialvip.domain.exception;

/**
 * Exceção para falhas de persistência (substitui org.eclipse.aether.RepositoryException).
 */
public class RepositoryException extends CelestialVipException {

    public RepositoryException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public String getMessageKey() {
        return null;
    }
}
