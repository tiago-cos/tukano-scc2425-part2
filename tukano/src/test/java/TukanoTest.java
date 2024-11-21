import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import clients.rest.RestBlobsClient;
import clients.rest.RestShortsClient;
import clients.rest.RestUsersClient;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import tukano.api.util.Result;
import tukano.models.ShortDTO;
import tukano.models.UserDTO;

public class TukanoTest {
	private static RestBlobsClient blobs;
	private static RestUsersClient users;
	private static RestShortsClient shorts;
	private static String serverURI;

	@BeforeAll
	public static void startServer() throws Exception {
		serverURI = String.format("http://192.168.49.2:30080/rest");

		blobs = new RestBlobsClient(serverURI);
		users = new RestUsersClient(serverURI);
		shorts = new RestShortsClient(serverURI);
	}

	@Test
	public void createUser() {
		UserDTO liskov = new UserDTO("liskov", "54321", "liskov@mit.edu", "Barbara Liskov");

		Result<String> result = users.createUser(liskov);
		assert (result.isOK());
		assertEquals(result.value(), "liskov");
	}

	@Test
	public void createUserConflict() {
		UserDTO wales = new UserDTO("wales", "12345", "jimmy@wikipedia.pt", "Jimmy Wales");
		UserDTO doppelganger = new UserDTO("wales", "12345", "eviljimmy@wikipedia.pt", "Evil Jimmy Wales");

		Result<String> result = users.createUser(wales);
		assert (result.isOK());
		assertEquals(result.value(), "wales");

		result = users.createUser(doppelganger);
		assert (!result.isOK());
		assertEquals(result.error(), Result.ErrorCode.CONFLICT);
	}

	@Test
	public void createUserBadRequest() {
		UserDTO bad = new UserDTO(null, null, null, null);

		Result<String> result = users.createUser(bad);
		assert (!result.isOK());
		assertEquals(result.error(), Result.ErrorCode.BAD_REQUEST);
	}

	@Test
	public void getUser() {
		UserDTO knuth = new UserDTO("knuth", "31415", "knuthkills@murder.org", "Donald Knuth");

		Result<String> result = users.createUser(knuth);
		assert (result.isOK());
		assertEquals(result.value(), "knuth");

		Result<UserDTO> result2 = users.getUser("knuth", "31415");

		assert (result2.isOK());
		assertEquals(result2.value(), knuth);
	}

	@Test
	public void getUserForbidden() {
		UserDTO turing = new UserDTO("turing", "98765", "smart@computer.me", "Alan Turing");

		Result<String> result = users.createUser(turing);
		assert (result.isOK());
		assertEquals(result.value(), "turing");

		Result<UserDTO> result2 = users.getUser("turing", "12345");
		assert (!result2.isOK());
		assertEquals(result2.error(), Result.ErrorCode.FORBIDDEN);
	}

	@Test
	public void getUserNotFound() {
		Result<UserDTO> result = users.getUser("nonexistent", "password");
		assert (!result.isOK());
		assertEquals(result.error(), Result.ErrorCode.NOT_FOUND);
	}

	@Test
	public void updateUser() {
		UserDTO torvalds = new UserDTO("torvalds", "12345", "linux@nerd.pingu", "Linus Torvalds");

		Result<String> result = users.createUser(torvalds);
		assert (result.isOK());
		assertEquals(result.value(), "torvalds");

		UserDTO newTorvalds = new UserDTO("torvalds", "12345", "linux@jock.pingu", null);

		Result<UserDTO> result2 = users.updateUser("torvalds", "12345", newTorvalds);
		assert (result2.isOK());
		assertEquals(result2.value(), newTorvalds);

		assertEquals(result2.value().getEmail(), "linux@jock.pingu");
		assertEquals(result2.value().getDisplayName(), "Linus Torvalds");
	}

	@Test
	public void updateUserForbidden() {
		UserDTO stallman = new UserDTO("stallman", "54321", "lenght@tallman.high", "Richard Stallman");

		Result<String> result = users.createUser(stallman);
		assert (result.isOK());
		assertEquals(result.value(), "stallman");

		UserDTO newStallman = new UserDTO("stallman", "54321", "length@tallman.high", "[REDACTED]");

		Result<UserDTO> result2 = users.updateUser("stallman", "12345", newStallman);
		assert (!result2.isOK());
		assertEquals(result2.error(), Result.ErrorCode.FORBIDDEN);
	}

	@Test
	public void updateUserNotFound() {
		UserDTO newman = new UserDTO("newman", "12345", "baby@justborn.wah", "Newman");

		Result<UserDTO> result = users.updateUser("newman", "12345", newman);
		assert (!result.isOK());
		assertEquals(result.error(), Result.ErrorCode.NOT_FOUND);
	}

	@Test
	public void deleteUser() {
		UserDTO mary = new UserDTO("mary", "bleh", "hacker@evilcorp.scheme", "Mary Poppendieck");

		Result<String> result = users.createUser(mary);
		assert (result.isOK());
		assertEquals(result.value(), "mary");

		Result<UserDTO> result2 = users.deleteUser("mary", "bleh");
		assert (result2.isOK());
		assertEquals(result2.value(), mary);

		Result<UserDTO> result3 = users.getUser("mary", "bleh");
		assert (!result3.isOK());
		assertEquals(result3.error(), Result.ErrorCode.NOT_FOUND);
	}

	@Test
	public void deleteUserNotFound() {
		Result<UserDTO> result = users.deleteUser("nonexistent", "password");
		assert (!result.isOK());
		assertEquals(result.error(), Result.ErrorCode.NOT_FOUND);
	}

	@Test
	public void deleteUserForbidden() {
		UserDTO paul = new UserDTO("paul", "cwycwy", "verypaul@paul.com", "Paul Blowfish");

		Result<String> result = users.createUser(paul);
		assert (result.isOK());
		assertEquals(result.value(), "paul");

		Result<UserDTO> result2 = users.deleteUser("paul", "happy");
		assert (!result2.isOK());
		assertEquals(result2.error(), Result.ErrorCode.FORBIDDEN);
	}

	@Test
	public void searchUser() {
		UserDTO dijkstra = new UserDTO("dijkstra", "12345", "efficient@walking.short", "Edsger Dijkstra");

		Result<String> result = users.createUser(dijkstra);
		assert (result.isOK());
		assertEquals(result.value(), "dijkstra");

		Result<List<UserDTO>> result2 = users.searchUsers("dijkstra");
		assert (result2.isOK());
		assertEquals(result2.value().size(), 1);

		UserDTO miniDijkstra = new UserDTO("dijkstraJr", "12345", "disappointment@dad.left", "Marcus Dijkstra");

		Result<String> result3 = users.createUser(miniDijkstra);
		assert (result3.isOK());
		assertEquals(result3.value(), "dijkstraJr");

		Result<List<UserDTO>> result4 = users.searchUsers("dijkstra");
		assert (result4.isOK());
		assertEquals(result4.value().size(), 2);
	}

	@Test
	public void createShort() {
		UserDTO grace = new UserDTO("hopper", "12345", "jumping@skipping.fun", "Grace Hopper");

		Result<String> result = users.createUser(grace);
		assert (result.isOK());
		assertEquals(result.value(), "hopper");

		Result<ShortDTO> result2 = shorts.createShort("hopper", "12345");
		assert (result2.isOK());
		assertEquals(result2.value().getOwnerId(), "hopper");
		assertEquals(result2.value().getTotalLikes(), 0);
	}

	@Test
	public void createShortUserNotFound() {
		Result<ShortDTO> result = shorts.createShort("notexists", "something");
		assertEquals(result.error(), Result.ErrorCode.NOT_FOUND);
	}

	@Test
	public void createShortForbidden() {
		UserDTO donald = new UserDTO("donald", "duck", "donald@example.com", "donald");

		Result<String> result = users.createUser(donald);
		assert (result.isOK());
		assertEquals(result.value(), "donald");

		Result<ShortDTO> result2 = shorts.createShort("donald", "something");
		assertEquals(result2.error(), Result.ErrorCode.FORBIDDEN);
	}

	@Test
	public void getShort() {
		UserDTO scrooge = new UserDTO("scrooge", "mcduck", "scrooge@example.com", "scrooge");

		Result<String> result = users.createUser(scrooge);
		assert (result.isOK());
		assertEquals(result.value(), "scrooge");

		Result<ShortDTO> result2 = shorts.createShort("scrooge", "mcduck");
		assert (result2.isOK());
		assertEquals(result2.value().getOwnerId(), "scrooge");

		Result<ShortDTO> result3 = shorts.getShort(result2.value().getShortId());
		assert (result3.isOK());
		assertEquals(result3.value(), result2.value());
	}

	@Test
	public void getShortNotFound() {
		Result<ShortDTO> result = shorts.getShort("nonexistent");
		assertEquals(result.error(), Result.ErrorCode.NOT_FOUND);
	}

	@Test
	public void deleteShort() throws Exception {
		UserDTO daisy = new UserDTO("daisy", "duck", "daisy@example.com", "daisy");

		Result<String> result = users.createUser(daisy);
		assert (result.isOK());
		assertEquals(result.value(), "daisy");

		Result<ShortDTO> result2 = shorts.createShort("daisy", "duck");
		assert (result2.isOK());
		assertEquals(result2.value().getOwnerId(), "daisy");

		Result<Void> result3 = shorts.deleteShort(result2.value().getShortId(), "duck");
		assert (result3.isOK());

		Result<ShortDTO> result4 = shorts.getShort(result2.value().getShortId());
		assertEquals(result4.error(), Result.ErrorCode.NOT_FOUND);
	}

	@Test
	public void deleteShortNotFound() {
		Result<Void> result = shorts._deleteShort("nonexistent", "password");
		assertEquals(result.error(), Result.ErrorCode.NOT_FOUND);
	}

	@Test
	public void deleteShortForbidden() {
		UserDTO huey = new UserDTO("huey", "duck", "huey@example.com", "huey");

		Result<String> result = users.createUser(huey);
		assert (result.isOK());
		assertEquals(result.value(), "huey");

		Result<ShortDTO> result2 = shorts.createShort("huey", "duck");
		assert (result2.isOK());
		assertEquals(result2.value().getOwnerId(), "huey");

		Result<Void> result3 = shorts.deleteShort(result2.value().getShortId(), "something");
		assertEquals(result3.error(), Result.ErrorCode.FORBIDDEN);
	}

	@Test
	public void getShorts() {
		UserDTO dewey = new UserDTO("dewey", "duck", "dewey@example.com", "dewey");

		Result<String> result = users.createUser(dewey);
		assert (result.isOK());
		assertEquals(result.value(), "dewey");

		Result<ShortDTO> result2 = shorts.createShort("dewey", "duck");
		assert (result2.isOK());
		ShortDTO short1 = result2.value();
		assertEquals(short1.getOwnerId(), "dewey");

		Result<ShortDTO> result3 = shorts.createShort("dewey", "duck");
		assert (result3.isOK());
		ShortDTO short2 = result3.value();
		assertEquals(short2.getOwnerId(), "dewey");

		Result<List<String>> result4 = shorts.getShorts("dewey");
		assert (result4.isOK());
		List<String> shortIds = result4.value();
		assertEquals(shortIds.size(), 2);
		assert (shortIds.contains(short1.getShortId()));
		assert (shortIds.contains(short2.getShortId()));
	}

	@Test
	public void getShortsNotFound() {
		Result<List<String>> result = shorts.getShorts("nonexistent");
		assertEquals(result.error(), Result.ErrorCode.NOT_FOUND);
	}

	@Test
	public void follow() {
		UserDTO luey = new UserDTO("louie", "duck", "luey@example.com", "louie");
		UserDTO webby = new UserDTO("webby", "vanderquack", "webby@example.com", "webby");

		Result<String> result = users.createUser(luey);
		assert (result.isOK());
		assertEquals(result.value(), "louie");

		Result<String> result2 = users.createUser(webby);
		assert (result2.isOK());
		assertEquals(result2.value(), "webby");

		// following
		Result<Void> result3 = shorts.follow("webby", "louie", true, "vanderquack");
		assert (result3.isOK());

		// checking followers
		Result<List<String>> result4 = shorts.followers("louie", "duck");
		assert (result4.isOK());
		assertEquals(result4.value(), List.of("webby"));

		// unfollowing
		Result<Void> result5 = shorts.follow("webby", "louie", false, "vanderquack");
		assert (result5.isOK());

		// checking that the user has no followers
		Result<List<String>> result6 = shorts.followers("louie", "duck");
		assert (result6.isOK());
		assertTrue(result6.value().isEmpty());
	}

	@Test
	public void followNotFound() {
		UserDTO goofy = new UserDTO("goofy", "goof", "goofy@example.com", "goofy");
		UserDTO max = new UserDTO("max", "goof", "max@example.com", "max");

		Result<String> result = users.createUser(goofy);
		assert (result.isOK());
		assertEquals(result.value(), "goofy");

		Result<String> result2 = users.createUser(max);
		assert (result2.isOK());
		assertEquals(result2.value(), "max");

		Result<Void> result3 = shorts.follow("max", "something", true, "goof");
		assertEquals(result3.error(), Result.ErrorCode.NOT_FOUND);

		Result<Void> result4 = shorts.follow("something", "goofy", true, "goof");
		assertEquals(result4.error(), Result.ErrorCode.NOT_FOUND);
	}

	@Test
	public void followForbidden() {
		UserDTO launchpad = new UserDTO("launchpad", "mcquack", "launchpad@example.com", "launchpad");
		UserDTO darkwing = new UserDTO("darkwing", "duck", "darkwing@example.com", "darkwing");

		Result<String> result = users.createUser(launchpad);
		assert (result.isOK());
		assertEquals(result.value(), "launchpad");

		Result<String> result2 = users.createUser(darkwing);
		assert (result2.isOK());
		assertEquals(result2.value(), "darkwing");

		Result<Void> result3 = shorts.follow("launchpad", "darkwing", true, "something");
		assertEquals(result3.error(), Result.ErrorCode.FORBIDDEN);
	}

	@Test
	public void followers() {
		UserDTO gyro = new UserDTO("gyro", "gearloose", "gyro@example.com", "gyro");
		UserDTO gladstone = new UserDTO("gladstone", "gander", "gladstone@example.com", "gladstone");

		Result<String> result = users.createUser(gyro);
		assert (result.isOK());
		assertEquals(result.value(), "gyro");

		Result<String> result2 = users.createUser(gladstone);
		assert (result2.isOK());
		assertEquals(result2.value(), "gladstone");

		Result<Void> result3 = shorts.follow("gladstone", "gyro", true, "gander");
		assert (result3.isOK());

		Result<List<String>> result4 = shorts.followers("gyro", "gearloose");
		assert (result4.isOK());
		assertEquals(result4.value(), List.of("gladstone"));
	}

	@Test
	public void followersNotFound() {
		Result<List<String>> result = shorts.followers("something", "something");
		assertEquals(result.error(), Result.ErrorCode.NOT_FOUND);
	}

	@Test
	public void followersForbidden() {
		UserDTO fenton = new UserDTO("fenton", "crackshellcabrera", "fenton@example.com", "fenton");

		Result<String> result = users.createUser(fenton);
		assert (result.isOK());
		assertEquals(result.value(), "fenton");

		Result<List<String>> result2 = shorts.followers("fenton", "something");
		assertEquals(result2.error(), Result.ErrorCode.FORBIDDEN);
	}

	@Test
	public void like() {
		UserDTO flintheart = new UserDTO("flintheart", "glomgold", "flintheart@example.com", "flintheart");

		Result<String> result = users.createUser(flintheart);
		assert (result.isOK());
		assertEquals(result.value(), "flintheart");

		Result<ShortDTO> result2 = shorts.createShort("flintheart", "glomgold");
		assert (result2.isOK());
		ShortDTO short1 = result2.value();
		assertEquals(short1.getOwnerId(), "flintheart");

		Result<Void> result3 = shorts.like(short1.getShortId(), "flintheart", true, "glomgold");
		assert (result3.isOK());

		Result<List<String>> result4 = shorts.likes(short1.getShortId(), "glomgold");
		assert (result4.isOK());
		assertEquals(result4.value(), List.of(short1.getOwnerId()));

		Result<Void> result5 = shorts.like(short1.getShortId(), "flintheart", false, "glomgold");
		assert (result5.isOK());

		Result<List<String>> result6 = shorts.likes(short1.getShortId(), "glomgold");
		assert (result6.isOK());
		assertTrue(result6.value().isEmpty());
	}

	@Test
	public void likeNotFound() {
		UserDTO harry = new UserDTO("harry", "potter", "harry@example.com", "harry");
		UserDTO ron = new UserDTO("ron", "weasley", "ron@example.com", "ron");

		Result<String> result = users.createUser(harry);
		assert (result.isOK());
		assertEquals(result.value(), "harry");

		Result<String> result2 = users.createUser(ron);
		assert (result2.isOK());
		assertEquals(result2.value(), "ron");

		Result<ShortDTO> result3 = shorts.createShort("harry", "potter");
		assert (result3.isOK());
		ShortDTO shorty = result3.value();
		assertEquals(shorty.getOwnerId(), "harry");

		// the short to which the like is being added doesnt exist
		Result<Void> result4 = shorts.like("something", "harry", true, "potter");
		assertEquals(result4.error(), Result.ErrorCode.NOT_FOUND);

		// trying to like a short where the user doesnt exist
		Result<Void> result6 = shorts.like(shorty.getShortId(), "something", true, "potter");
		assertEquals(result6.error(), Result.ErrorCode.NOT_FOUND);

		// trying to like where neither short nor user exist
		Result<Void> result7 = shorts.like("something", "something", true, "potter");
		assertEquals(result7.error(), Result.ErrorCode.NOT_FOUND);
	}

	@Test
	public void likeConflict() {
		UserDTO hermione = new UserDTO("hermione", "granger", "hermione@example.com", "hermione");

		Result<String> result = users.createUser(hermione);
		assert (result.isOK());
		assertEquals(result.value(), "hermione");

		Result<ShortDTO> result2 = shorts.createShort("hermione", "granger");
		assert (result2.isOK());
		ShortDTO shorty = result2.value();
		assertEquals(shorty.getOwnerId(), "hermione");

		Result<Void> result3 = shorts.like(shorty.getShortId(), "hermione", true, "granger");
		assert (result3.isOK());

		Result<Void> result4 = shorts.like(shorty.getShortId(), "hermione", true, "granger");
		assertEquals(result4.error(), Result.ErrorCode.CONFLICT);

		Result<List<String>> result5 = shorts.likes(shorty.getShortId(), "granger");
		assert (result5.isOK());
		assertEquals(result5.value().size(), 1);
	}

	@Test
	public void likeForbidden() {
		UserDTO ginny = new UserDTO("ginny", "weasley", "ginny@example.com", "ginny");

		Result<String> result = users.createUser(ginny);
		assert (result.isOK());
		assertEquals(result.value(), "ginny");

		Result<ShortDTO> result2 = shorts.createShort("ginny", "weasley");
		assert (result2.isOK());
		assertEquals(result2.value().getOwnerId(), "ginny");

		Result<Void> result3 = shorts.like(result2.value().getShortId(), "ginny", true, "something");
		assertEquals(result3.error(), Result.ErrorCode.FORBIDDEN);
	}

	@Test
	public void likes() {
		UserDTO luna = new UserDTO("luna", "lovegood", "luna@example.com", "luna");
		UserDTO neville = new UserDTO("neville", "longbottom", "neville@example.com", "neville");

		Result<String> result = users.createUser(luna);
		assert (result.isOK());
		assertEquals(result.value(), "luna");

		Result<String> result2 = users.createUser(neville);
		assert (result2.isOK());
		assertEquals(result2.value(), "neville");

		Result<ShortDTO> result3 = shorts.createShort("luna", "lovegood");
		assert (result3.isOK());
		assertEquals(result3.value().getOwnerId(), "luna");

		Result<Void> result4 = shorts.like(result3.value().getShortId(), "luna", true, "lovegood");
		assert (result4.isOK());

		Result<Void> result5 = shorts.like(result3.value().getShortId(), "neville", true, "longbottom");
		assert (result5.isOK());

		Result<List<String>> result6 = shorts.likes(result3.value().getShortId(), "lovegood");
		assert (result6.isOK());
		assertEquals(result6.value().size(), 2);
		assertTrue(result6.value().contains("luna"));
		assertTrue(result6.value().contains("neville"));

		Result<Void> result7 = shorts.like(result3.value().getShortId(), "neville", false, "longbottom");
		assert (result7.isOK());

		Result<List<String>> result8 = shorts.likes(result3.value().getShortId(), "lovegood");
		assert (result8.isOK());
		assertEquals(result8.value().size(), 1);
		assertTrue(result8.value().contains("luna"));
	}

	@Test
	public void likesCount() {
		UserDTO cho = new UserDTO("cho", "chang", "cho@example.com", "cho");
		UserDTO cedric = new UserDTO("cedric", "diggory", "cedric@example.com", "cedric");

		Result<String> result = users.createUser(cho);
		assert (result.isOK());
		assertEquals(result.value(), "cho");

		Result<String> result2 = users.createUser(cedric);
		assert (result2.isOK());
		assertEquals(result2.value(), "cedric");

		Result<ShortDTO> result3 = shorts.createShort("cho", "chang");
		assert (result3.isOK());
		assertEquals(result3.value().getOwnerId(), "cho");

		Result<Void> result4 = shorts.like(result3.value().getShortId(), "cho", true, "chang");
		assert (result4.isOK());

		Result<Void> result5 = shorts.like(result3.value().getShortId(), "cedric", true, "diggory");
		assert (result5.isOK());

		Result<ShortDTO> result6 = shorts.getShort(result3.value().getShortId());
		assert (result6.isOK());
		assertEquals(result6.value().getTotalLikes(), 2);

		Result<Void> result7 = shorts.like(result3.value().getShortId(), "cedric", false, "diggory");
		assert (result7.isOK());

		Result<ShortDTO> result8 = shorts.getShort(result3.value().getShortId());
		assert (result8.isOK());
		assertEquals(result8.value().getTotalLikes(), 1);
	}

	@Test
	public void likesNotFound() {
		Result<List<String>> result = shorts.likes("something", "something");
		assertEquals(result.error(), Result.ErrorCode.NOT_FOUND);
	}

	@Test
	public void likesForbidden() {
		UserDTO draco = new UserDTO("draco", "malfoy", "draco@example.com", "draco");

		Result<String> result = users.createUser(draco);
		assert (result.isOK());
		assertEquals(result.value(), "draco");

		Result<ShortDTO> result2 = shorts.createShort("draco", "malfoy");
		assert (result2.isOK());
		assertEquals(result2.value().getOwnerId(), "draco");

		Result<List<String>> result3 = shorts.likes(result2.value().getShortId(), "something");
		assertEquals(result3.error(), Result.ErrorCode.FORBIDDEN);
	}

	@Test
	public void getFeed() {
		// user whose feed will be displayed
		UserDTO james = new UserDTO("james", "potter", "james@example.com", "james");
		// users that will be followed
		UserDTO sirius = new UserDTO("sirius", "black", "sirius@example.com", "sirius");
		UserDTO remus = new UserDTO("remus", "lupin", "remus@example.com", "remus");

		Result<String> result = users.createUser(james);
		assert (result.isOK());
		assertEquals(result.value(), "james");

		Result<String> result2 = users.createUser(sirius);
		assert (result2.isOK());
		assertEquals(result2.value(), "sirius");

		Result<String> result3 = users.createUser(remus);
		assert (result3.isOK());
		assertEquals(result3.value(), "remus");

		// shorts sirius
		Result<ShortDTO> result4 = shorts.createShort("sirius", "black");
		assert (result4.isOK());
		assertEquals(result4.value().getOwnerId(), "sirius");

		Result<ShortDTO> result5 = shorts.createShort("sirius", "black");
		assert (result5.isOK());
		assertEquals(result5.value().getOwnerId(), "sirius");

		// shorts of remus
		Result<ShortDTO> result6 = shorts.createShort("remus", "lupin");
		assert (result6.isOK());
		assertEquals(result6.value().getOwnerId(), "remus");

		Result<ShortDTO> result7 = shorts.createShort("remus", "lupin");
		assert (result7.isOK());
		assertEquals(result7.value().getOwnerId(), "remus");

		// james follows sirius and remus
		Result<Void> result8 = shorts.follow("james", "sirius", true, "potter");
		assert (result8.isOK());

		Result<Void> result9 = shorts.follow("james", "remus", true, "potter");
		assert (result9.isOK());

		// check the feed of james
		Result<List<String>> result10 = shorts.getFeed("james", "potter");
		assert (result10.isOK());
		assert (result10.value().contains(result7.value().getShortId()));
		assert (result10.value().contains(result6.value().getShortId()));
		assert (result10.value().contains(result5.value().getShortId()));
		assert (result10.value().contains(result4.value().getShortId()));
	}

	@Test
	public void getFeedNotFound() {
		Result<List<String>> result = shorts.getFeed("something", "something");
		assertEquals(result.error(), Result.ErrorCode.NOT_FOUND);
	}

	@Test
	public void getFeedForbidden() {
		UserDTO lily = new UserDTO("lily", "evans", "lily@example.com", "lily");

		Result<String> result = users.createUser(lily);
		assert (result.isOK());
		assertEquals(result.value(), "lily");

		Result<List<String>> result2 = shorts.getFeed("lily", "something");
		assertEquals(result2.error(), Result.ErrorCode.FORBIDDEN);
	}

	@Test
	public void deleteAllShorts() throws Exception {
		UserDTO minerva = new UserDTO("minerva", "mcgonagall", "minerva@example.com", "minerva");

		Result<String> result = users.createUser(minerva);
		assert (result.isOK());
		assertEquals(result.value(), "minerva");

		Result<ShortDTO> result2 = shorts.createShort("minerva", "mcgonagall");
		assert (result2.isOK());
		assertEquals(result2.value().getOwnerId(), "minerva");

		Result<ShortDTO> result3 = shorts.createShort("minerva", "mcgonagall");
		assert (result3.isOK());
		assertEquals(result3.value().getOwnerId(), "minerva");

		Result<List<String>> result4 = shorts.getShorts("minerva");
		assert (result4.isOK());
		assertEquals(result4.value().size(), 2);
		assertTrue(result4.value().contains(result2.value().getShortId()));
		assertTrue(result4.value().contains(result3.value().getShortId()));

		Result<UserDTO> result5 = users.deleteUser("minerva", "mcgonagall");
		Thread.sleep(1000);
		assert (result5.isOK());
		assertEquals(result5.value(), minerva);

		Result<List<String>> result6 = shorts.getShorts("minerva");
		assertEquals(result6.error(), Result.ErrorCode.NOT_FOUND);
	}

	@Test
	public void deleteAllLikes() throws Exception {
		UserDTO dobby = new UserDTO("dobby", "theelf", "dobby@exaple.com", "dobby");
		UserDTO hedwig = new UserDTO("hedwig", "theowl", "hedwig@exaple.com", "hedwig");

		Result<String> result = users.createUser(dobby);
		assert (result.isOK());
		assertEquals(result.value(), "dobby");

		Result<String> result2 = users.createUser(hedwig);
		assert (result2.isOK());
		assertEquals(result2.value(), "hedwig");

		Result<ShortDTO> result3 = shorts.createShort("dobby", "theelf");
		assert (result3.isOK());
		assertEquals(result3.value().getOwnerId(), "dobby");

		Result<Void> result4 = shorts.like(result3.value().getShortId(), "hedwig", true, "theowl");
		assert (result4.isOK());

		Result<List<String>> result5 = shorts.likes(result3.value().getShortId(), "theelf");
		assert (result5.isOK());
		assertTrue(result5.value().contains("hedwig"));

		Result<UserDTO> result6 = users.deleteUser("hedwig", "theowl");
		Thread.sleep(1000);
		assert (result6.isOK());

		Result<List<String>> result7 = shorts.likes(result3.value().getShortId(), "theelf");
		assert (result7.isOK());
		assertFalse(result7.value().contains("hedwig"));
	}

	@Test
	public void deleteUserFollowings() throws Exception {
		UserDTO hagrid = new UserDTO("rubeus", "hagrid", "hagrid@example.com", "hagrid");
		UserDTO aragog = new UserDTO("aragog", "thespider", "aragog@example.com", "aragog");

		Result<String> result = users.createUser(hagrid);
		assert (result.isOK());
		assertEquals(result.value(), "rubeus");

		Result<String> result2 = users.createUser(aragog);
		assert (result2.isOK());
		assertEquals(result2.value(), "aragog");

		Result<Void> result3 = shorts.follow("aragog", "rubeus", true, "thespider");
		assert (result3.isOK());

		Result<List<String>> result4 = shorts.followers("rubeus", "hagrid");
		assert (result4.isOK());
		assertEquals(result4.value().size(), 1);
		assertTrue(result4.value().contains("aragog"));

		Result<UserDTO> result5 = users.deleteUser("aragog", "thespider");
		Thread.sleep(1000);
		assert (result5.isOK());

		Result<List<String>> result6 = shorts.followers("rubeus", "hagrid");
		assert (result6.isOK());
		assertTrue(result6.value().isEmpty());
	}

	@Test
	public void uploadDownloadBlobs() {
		UserDTO percy = new UserDTO("percy", "jackson", "percy@example.com", "percy");
		Result<String> result = users.createUser(percy);
		assert (result.isOK());
		assertEquals(result.value(), "percy");

		Result<ShortDTO> result2 = shorts.createShort("percy", "jackson");
		assert (result2.isOK());
		ShortDTO shorty = result2.value();
		assertEquals(shorty.getOwnerId(), "percy");

		String blobId = shorty.getBlobUrl().split("\\?token=")[0];
		String token = shorty.getBlobUrl().split("\\?token=")[1];

		Result<Void> result3 = blobs.upload(blobId, shorty.getBlobUrl().getBytes(), token);
		assert (result3.isOK());

		Result<Void> result4 = blobs.upload(blobId, shorty.getBlobUrl().getBytes(), token);
		assert (result4.isOK());

		Result<byte[]> result5 = blobs.download(blobId, token);
		assert (result5.isOK());
		assert (Arrays.equals(shorty.getBlobUrl().getBytes(), result5.value()));
	}

	@Test
	public void uploadBlobsConflict() {
		UserDTO annabeth = new UserDTO("annabeth", "chase", "annabeth@example.com", "annabeth");
		Result<String> result = users.createUser(annabeth);
		assert (result.isOK());
		assertEquals(result.value(), "annabeth");

		Result<ShortDTO> result2 = shorts.createShort("annabeth", "chase");
		assert (result2.isOK());
		ShortDTO shorty = result2.value();
		assertEquals(shorty.getOwnerId(), "annabeth");

		String blobId = shorty.getBlobUrl().split("\\?token=")[0];
		String token = shorty.getBlobUrl().split("\\?token=")[1];

		Result<Void> result3 = blobs.upload(blobId, shorty.getBlobUrl().getBytes(), token);
		assert (result3.isOK());

		Result<Void> result4 = blobs.upload(blobId, "annabeth".getBytes(), token);
		assertEquals(result4.error(), Result.ErrorCode.CONFLICT);
	}

	@Test
	public void uploadBlobsForbidden() {
		UserDTO grover = new UserDTO("grover", "underwood", "grover@example.com", "grover");
		Result<String> result = users.createUser(grover);
		assert (result.isOK());
		assertEquals(result.value(), "grover");

		Result<ShortDTO> result2 = shorts.createShort("grover", "underwood");
		assert (result2.isOK());
		ShortDTO shorty = result2.value();
		assertEquals(shorty.getOwnerId(), "grover");

		String blobId = shorty.getBlobUrl().split("\\?token=")[0];

		Result<Void> result3 = blobs.upload(blobId, shorty.getBlobUrl().getBytes(), "token");
		assertEquals(result3.error(), Result.ErrorCode.FORBIDDEN);
	}

	@Test
	public void downloadBlobsForbidden() {
		Result<byte[]> result = blobs.download(serverURI + "/blobs/blobId", "something");
		assertEquals(result.error(), Result.ErrorCode.FORBIDDEN);
	}

	@Test
	public void deleteBlobs() {
		UserDTO jason = new UserDTO("jason", "grace", "jason@example.com", "jason");
		Result<String> result = users.createUser(jason);
		assert (result.isOK());
		assertEquals(result.value(), "jason");

		Result<ShortDTO> result2 = shorts.createShort("jason", "grace");
		assert (result2.isOK());
		ShortDTO shorty = result2.value();
		assertEquals(shorty.getOwnerId(), "jason");

		String blobId = shorty.getBlobUrl().split("\\?token=")[0];
		String token = shorty.getBlobUrl().split("\\?token=")[1];

		Result<Void> result3 = blobs.upload(blobId, shorty.getBlobUrl().getBytes(), token);
		assert (result3.isOK());

		Result<Void> result4 = blobs.delete(blobId, token);
		assert (result4.isOK());

		Result<byte[]> result5 = blobs.download(blobId, token);
		assertEquals(result5.error(), Result.ErrorCode.NOT_FOUND);
	}

	@Test
	public void deleteBlobsForbidden() {
		Result<Void> result = blobs.delete(serverURI + "/blobs/blobId", "");
		assertEquals(result.error(), Result.ErrorCode.FORBIDDEN);
	}

	@Test
	public void deleteAllBlobs() throws Exception {
		UserDTO frank = new UserDTO("frank", "zhang", "frank@example.com", "frank");
		Result<String> result = users.createUser(frank);
		assert (result.isOK());
		assertEquals(result.value(), "frank");

		Result<ShortDTO> result2 = shorts.createShort("frank", "zhang");
		assert (result2.isOK());
		ShortDTO shorty1 = result2.value();
		assertEquals(shorty1.getOwnerId(), "frank");

		String blobId1 = shorty1.getBlobUrl().split("\\?token=")[0];
		String token1 = shorty1.getBlobUrl().split("\\?token=")[1];

		Result<ShortDTO> result3 = shorts.createShort("frank", "zhang");
		assert (result3.isOK());
		ShortDTO shorty2 = result2.value();
		assertEquals(shorty2.getOwnerId(), "frank");

		String blobId2 = shorty2.getBlobUrl().split("\\?token=")[0];
		String token2 = shorty2.getBlobUrl().split("\\?token=")[1];

		Result<Void> result4 = blobs.upload(blobId1, shorty1.getBlobUrl().getBytes(), token1);
		assert (result4.isOK());

		Result<Void> result5 = blobs.upload(blobId2, shorty2.getBlobUrl().getBytes(), token2);
		assert (result5.isOK());

		Result<UserDTO> result6 = users.deleteUser("frank", "zhang");
		Thread.sleep(1000);
		assert (result6.isOK());
		assertEquals(result6.value().getUserId(), "frank");

		Result<byte[]> result7 = blobs.download(blobId1, token1);
		assertEquals(result7.error(), Result.ErrorCode.NOT_FOUND);

		Result<byte[]> result8 = blobs.download(blobId2, token2);
		assertEquals(result8.error(), Result.ErrorCode.NOT_FOUND);
	}
}
