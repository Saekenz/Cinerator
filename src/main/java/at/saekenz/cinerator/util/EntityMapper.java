package at.saekenz.cinerator.util;

public interface EntityMapper<T, D> {

    public D toDTO(T entity);
}
