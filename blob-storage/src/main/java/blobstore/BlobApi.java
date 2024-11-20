package blobstore;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

@Path("/blobs")
public interface BlobApi {

	@POST
	@Path("/{blobId}")
	@Consumes(MediaType.APPLICATION_OCTET_STREAM)
	Response uploadBlob(@PathParam("blobId") String blobId, byte[] blob) throws NoSuchAlgorithmException, IOException;

	@DELETE
	@Path("/{blobId}")
	Response deleteBlob(@PathParam("blobId") String blobId) throws IOException;

	@GET
	@Path("/{blobId}")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	Response downloadBlob(@PathParam("blobId") String blobId) throws IOException;
}
