package io.jzheaux.springone2019.message.tenant;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.query.MongoEntityInformation;
import org.springframework.data.mongodb.repository.support.SimpleMongoRepository;
import org.springframework.util.ReflectionUtils;

public class TenantBaseRepository<T extends TenantAware> implements MongoRepository<T, UUID> {
	private final MongoEntityInformation<T, UUID> metadata;
	private final MongoRepository<T, UUID> read;
	private final MongoRepository<T, UUID> write;

	public TenantBaseRepository(MongoEntityInformation<T, UUID> metadata, MongoOperations mongo) {
		this.metadata = metadata;
		this.read = new SimpleMongoRepository<>(metadata, mongo);
		this.write = read;
	}

	@Override
	public <S extends T> S save(S entity) {
		entity = tenant(entity);
		return write.save(entity);
	}

	@Override
	public List<T> findAll() {
		Example<T> example = example();
		return read.findAll(example);
	}

	@Override
	public <S extends T> List<S> findAll(Example<S> example) {
		example = tenant(example);
		return read.findAll(example);
	}

	@Override
	public Optional<T> findById(UUID uuid) {
		T instance = instance(uuid);
		Example<T> example = Example.of(instance);
		return read.findOne(example);
	}

	@Override
	public List<T> findAll(Sort sort) {
		Example<T> example = example();
		return read.findAll(example, sort);
	}

	@Override
	public <S extends T> List<S> findAll(Example<S> example, Sort sort) {
		example = tenant(example);
		return read.findAll(example, sort);
	}

	@Override
	public Page<T> findAll(Pageable pageable) {
		Example<T> example = example();
		return read.findAll(example, pageable);
	}

	@Override
	public boolean existsById(UUID uuid) {
		Example<T> example = example();
		return read.exists(example);
	}

	@Override
	public Iterable<T> findAllById(Iterable<UUID> uuids) {
		throw new UnsupportedOperationException("find all by ids unsupported");
	}

	@Override
	public long count() {
		Example<T> example = example();
		return read.count(example);
	}

	@Override
	public <S extends T> Optional<S> findOne(Example<S> example) {
		example = tenant(example);
		return read.findOne(example);
	}

	@Override
	public <S extends T> Page<S> findAll(Example<S> example, Pageable pageable) {
		example = tenant(example);
		return read.findAll(example, pageable);
	}

	@Override
	public <S extends T> long count(Example<S> example) {
		example = tenant(example);
		return read.count(example);
	}

	@Override
	public <S extends T> boolean exists(Example<S> example) {
		example = tenant(example);
		return read.exists(example);
	}

	@Override
	public <S extends T> S insert(S entity) {
		entity = tenant(entity);
		return write.insert(entity);
	}

	@Override
	public <S extends T> List<S> insert(Iterable<S> entities) {
		entities.forEach(this::tenant);
		return write.insert(entities);
	}

	@Override
	public <S extends T> List<S> saveAll(Iterable<S> entities) {
		entities.forEach(this::tenant);
		return write.saveAll(entities);
	}

	@Override
	public void deleteAll() {
		throw new UnsupportedOperationException("delete not supported");
	}

	@Override
	public void deleteById(UUID uuid) {
		throw new UnsupportedOperationException("delete not supported");
	}

	@Override
	public void delete(T entity) {
		throw new UnsupportedOperationException("delete not supported");
	}

	@Override
	public void deleteAll(Iterable<? extends T> entities) {
		throw new UnsupportedOperationException("delete not supported");
	}

	private Example<T> example() {
		T instance = instance();
		return Example.of(instance);
	}

	private T instance() {
		try {
			return tenant(this.metadata.getJavaType().newInstance());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private T instance(UUID id) {
		try {
			T instance = instance();
			Field f = ReflectionUtils.findField(instance.getClass(), this.metadata.getIdAttribute());
			f.setAccessible(true);
			ReflectionUtils.setField(f, instance, id);
			return instance;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private <S extends T> Example<S> tenant(Example<S> example) {
		S entity = example.getProbe();
		return Example.of(tenant(entity));
	}

	private <S extends T> S tenant(S entity) {
		if (entity.getTenant() == null) {
			entity.setTenant(TenantResolver.resolve());
		}
		return entity;
	}
}
