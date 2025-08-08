package com.elhg;

import com.elhg.collections.ReservationCollection;
import com.elhg.collections.RestaurantCollection;
import com.elhg.dtos.Review;
import com.elhg.dtos.responses.RestaurantResponse;
import com.elhg.enums.PriceEnum;
import com.elhg.enums.ReservationStatusEnum;
import com.elhg.mappers.ReservationMapper;
import com.elhg.mappers.RestaurantMapper;
import com.elhg.records.Address;
import com.elhg.records.ContactInfo;
import com.elhg.services.definitions.ReservationBusinessService;
import com.elhg.services.definitions.ReservationCrudService;
import com.elhg.services.definitions.RestaurantCatalogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@SpringBootApplication
public class Application implements CommandLineRunner {

	@Autowired
	private RestaurantCatalogService restaurantCatalogService;

	@Autowired
	private ReservationCrudService reservationCrudService;

	@Autowired
	private ReservationBusinessService reservationBusinessService;

	@Autowired
	private ReservationMapper reservationMapper;

	@Autowired
	private RestaurantMapper restaurantMapper;

	public static void main(String[] args)  {
		SpringApplication.run(Application.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		//testRestaurantCatalogService();
		//testReservationCrudService();
		//testCRUDValidations();
		testMappers();
	}

	private void testMappers() {
		Address address = Address.builder()
				.street("Av. Reforma 123")
				.city("Ciudad de México")
				.postalCode("06600")
				.build();

		ContactInfo contactInfo = new ContactInfo(
				"555-123-4567",
				"contacto@restaurante.com",
				"www.restaurante.com"
		);

		List<Review> reviews = new ArrayList<>();

		Review review1 = new Review();
		review1.setCustomerId("customer-789");
		review1.setCustomerName("Ana López");
		review1.setRating(5);
		review1.setComment("Excelente comida y servicio. Muy recomendado!");
		review1.setTimestamp(Instant.now().minusSeconds(86400));

		Review review2 = new Review();
		review2.setCustomerId("customer-101");
		review2.setCustomerName("Carlos Mendoza");
		review2.setRating(4);
		review2.setComment("Buena experiencia, la comida estaba deliciosa.");
		review2.setTimestamp(Instant.now().minusSeconds(172800));

		reviews.add(review1);
		reviews.add(review2);

		RestaurantCollection restaurant = RestaurantCollection.builder()
				.id(UUID.randomUUID())
				.name("La Cocina Mexicana")
				.capacity(50)
				.address(address)
				.cuisineType("Mexicana")
				.priceRange(PriceEnum.MEDIUM)
				.openHours("09:00 - 22:00")
				.logoUrl("https://ejemplo.com/logo.png")
				.closeAt("22:00")
				.contactInfo(contactInfo)
				.reviews(reviews)
				.build();

		ReservationCollection reservation = ReservationCollection.builder()
				.id(UUID.randomUUID())
				.restaurantId(restaurant.getId().toString())
				.customerId("customer-123")
				.customerName("Juan Pérez")
				.customerEmail("juan.perez@email.com")
				.date("2025-07-15")
				.time("19:30")
				.partySize(4)
				.status(ReservationStatusEnum.PENDING)
				.notes("Mesa junto a la ventana si es posible")
				.build();

		System.out.println("Restaurant Collection: " + restaurant);
		System.out.println("Reservation Collection: " + reservation);


		restaurantMapper
				.toRestaurantResponseMono(Mono.just(restaurant))
				.subscribe(restaurantResponse -> System.out.println("Restaurant Response: " + restaurantResponse));

		reservationMapper
				.toReservationResponseMono(Mono.just(reservation))
				.subscribe(reservationResponse -> System.out.println("Reservation Response: " + reservationResponse));


	}

	private void testCRUDValidations(){
		final var parrillaModernaID = "0ee619ba-e95f-4103-99f7-ee9cdf831d90";
		final var unavailableID = "dfcbe98d-392b-4b93-9a49-27005223d15d";

		final var michaelReservation = createTestReservation(
				parrillaModernaID,
				"Michael Davis",
				2,
				"2025-06-16",
				"23:00",
				"Anniversary dinner - romantic table"
		);

		final var michaelReservationUnavailable = reservationCrudService
				.createReservation(michaelReservation)
				.block();

		System.out.println("Michael reservation: " + michaelReservationUnavailable.getId());

		/*final var michaelReservationToUpdate = reservationCrudService
				.readByReservationId(UUID.fromString("17d0812e-ba1f-49b3-a999-37a7cec581e1"))
				.block();

		michaelReservationToUpdate.setTime("23:30");
		michaelReservationToUpdate.setPartySize(3);

		final var michaelReservationUpdated = this.reservationCrudService
				.updateReservation(UUID.fromString("17d0812e-ba1f-49b3-a999-37a7cec581e1"), michaelReservationToUpdate)
				.block();

		System.out.println("Michael reservation updated: " + michaelReservationUpdated.getDate());
		System.out.println("Michael reservation updated: " + michaelReservationUpdated.getPartySize());*/

	}

	private void testReservationCrudService() throws InterruptedException {
		System.out.println("=== STARTING RESERVATION INSERT TESTS ===\n");

		final var parrillaModernaID = "0ee619ba-e95f-4103-99f7-ee9cdf831d90";
		final var cafeNostalgiaID = "be33011c-13dd-45b9-a60e-e9adb8f4e022";


		final var sarahReservation = createTestReservation(
				parrillaModernaID,
				"Sarah Johnson",
				4,
				"2025-06-15",
				"19:30",
				"Window table preferred"
		);

		final var michaelReservation = createTestReservation(
				parrillaModernaID,
				"Michael Davis",
				2,
				"2025-06-16",
				"20:00",
				"Anniversary dinner - romantic table"
		);

		final var emmaReservation = createTestReservation(
				cafeNostalgiaID,
				"Emma Wilson",
				6,
				"2025-06-17",
				"18:00",
				"Family birthday celebration"
		);

		final var sarahReservationCreated = reservationCrudService.createReservation(sarahReservation)
				.block();

		System.out.println("Sarah reservation: " + sarahReservationCreated.getId());

		final var michaelReservationCreated = reservationCrudService.createReservation(michaelReservation)
				.block();
		System.out.println("Michael reservation: " + michaelReservationCreated.getId());

		final var emmaReservationCreated = reservationCrudService.createReservation(emmaReservation)
				.block();
		System.out.println("Emma reservation: " + emmaReservationCreated.getId());

		System.out.println("=== FINISHED RESERVATION INSERT TESTS ===");

		System.out.println("=== INIT RESERVATION UPDATE TESTS ===");

		final var michaelReservationToUpdate = reservationCrudService.readByReservationId(michaelReservationCreated.getId()).block();

		michaelReservationToUpdate.setDate("20:30");
		michaelReservationToUpdate.setPartySize(3);

		final var michaelReservationUpdated = this.reservationCrudService.createReservation(michaelReservationToUpdate).block();

		System.out.println("michael reservation updated: " + michaelReservationUpdated.getDate());
		System.out.println("michael reservation updated: " + michaelReservationUpdated.getPartySize());

		System.out.println("=== FINISHED RESERVATION INSERT TESTS ===");


		Thread.sleep(60000);
		System.out.println("=== INIT RESERVATION DELETE TESTS ===");
		this.reservationCrudService.deleteReservation(michaelReservationCreated.getId()).block();
		System.out.println("=== FINISHED RESERVATION DELETE TESTS ===");


	}

	private ReservationCollection createTestReservation(String restaurantId, String customerName,
														int partySize, String date, String time, String notes) {
		return ReservationCollection.builder()
				.id(UUID.randomUUID())
				.restaurantId(restaurantId)
				.customerName(customerName)
				.partySize(partySize)
				.date(date)
				.time(time)
				.notes(notes)
				.build();
	}

	private void testRestaurantCatalogService() {
		restaurantCatalogService.readAll().subscribe(restaurant ->
				System.out.println("Restaurant: " + restaurant.toString()));

		restaurantCatalogService.readByCuisineType("Italian").subscribe(restaurant ->
				System.out.println("Restaurant by cuisine type: " + restaurant.toString()));

		restaurantCatalogService.readByCity("Boston").subscribe(restaurant ->
				System.out.println("Restaurant by city: " + restaurant.toString()));

		restaurantCatalogService.readByName("Pasta").subscribe(restaurant ->
				System.out.println("Restaurant by name: " + restaurant.toString()));

		restaurantCatalogService.readByPriceRangeIn(List.of(PriceEnum.CHEAP))
				.subscribe(restaurant ->
								System.out.println("Restaurant by price range: " + restaurant.toString()),
						error -> System.err.println("Error: " + error.getMessage()),
						() -> System.out.println("Completed reading restaurants by price range"));

	}
}
