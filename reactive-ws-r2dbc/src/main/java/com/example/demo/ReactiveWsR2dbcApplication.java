package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.annotation.Id;
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import org.springframework.data.r2dbc.repository.query.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import dev.miku.r2dbc.mysql.MySqlConnectionConfiguration;
import dev.miku.r2dbc.mysql.MySqlConnectionFactory;
import io.r2dbc.spi.ConnectionFactory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import reactor.core.publisher.Flux;

@SpringBootApplication
public class ReactiveWsR2dbcApplication {

	public static void main(String[] args) {
		SpringApplication.run(ReactiveWsR2dbcApplication.class, args);
	}

}

/*
@Repository
class ReservationRepository {
	private final ConnectionFactory connectionFactory;
	
	public ReservationRepository(ConnectionFactory connectionFactory) {
		this.connectionFactory = connectionFactory;
	}
	
	Mono<Void> deleteById (Integer Id) {
		return this.connection()
				.flatMapMany(c -> c.createStatement("delete from reservation where id = $1").bind("$1", Id)
						.execute()).then();
	}
	
	Flux<Reservation> findAll() {
		return this.connection()
				.flatMapMany(connection -> 
					Flux.from(connection.createStatement("select * from reservation").execute())
						.flatMap((Result r) -> r.map((row, rowMetadata) -> new Reservation(
								row.get("id", Integer.class),
								row.get("name", String.class)))));
	}
	
	Flux<Reservation> save (Reservation r) {
		Flux<? extends Result> flatMapMany = this.connection()
				.flatMapMany(conn -> conn.createStatement("insert into reservation(name) values ($1)")
						.bind("$1", r.getName())
						.add()
						.execute());
		
		return flatMapMany.switchMap(x -> Flux.just(new Reservation(r.getId(), r.getName())));
	}
	
	private Mono<Connection> connection () {
		return Mono.from(this.connectionFactory.create());	
	}
}
*/

@Data
@AllArgsConstructor
@NoArgsConstructor
class Reservation {
	
	@Id
	private Integer id;
	private String name;
}

interface ReservationRepository extends ReactiveCrudRepository<Reservation, Integer> {
	
	@Query("select * from reservation where name = :name")
	Flux<Reservation> findByName(String name);
	
}

@Configuration
@EnableR2dbcRepositories
class R2dbcConfiguration extends AbstractR2dbcConfiguration {
	
	private final ConnectionFactory connectionFactory;
	
	R2dbcConfiguration(ConnectionFactory cf) {
		this.connectionFactory = cf;
	}

	@Override
	public ConnectionFactory connectionFactory() {
		return this.connectionFactory;
	}
	
}

@Configuration
class ConnectionFactoryConfiguration {

	/*
	@Bean
	DatabaseClient databaseClient () {
		return DatabaseClient.create(connectionFactory());
	}*/
	
	@Bean
	ConnectionFactory connectionFactory () {
		MySqlConnectionConfiguration configuration = MySqlConnectionConfiguration.builder()
				.database("orders")
				.username("root")
				.password("root")
				.host("localhost")
				.port(8889)
				.build();
		return MySqlConnectionFactory.from(configuration);
	}
}
