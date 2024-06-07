package my.groupId.resource;

import io.quarkiverse.renarde.htmx.HxController;
import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;
import io.smallrye.common.annotation.Blocking;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import my.groupId.dto.ClientDto;
import my.groupId.mapstruct.ClientMapper;
import my.groupId.model.Client;
import my.groupId.repo.ClientRepo;
import org.jboss.resteasy.reactive.RestForm;

import java.util.List;

@Path("/")
@ApplicationScoped
public class ClientResource extends HxController {
    @Inject
    ClientRepo repo;
    @Inject
    ClientMapper mapper;
    @CheckedTemplate
    public static class Templates {
        public static native TemplateInstance modal(Client client, String crudMode);
        public static native TemplateInstance client(List<Client> clients);
        public static native TemplateInstance client$list(List<Client> clients);
    }

    @GET
    @Path("/client")
    @Blocking
    public TemplateInstance client() {
        if(isHxRequest()) {
            return Templates.client$list(repo.listAll());
        }
        return Templates.client(repo.listAll());
    }
//    @POST
//    @Path("client/save")
//    @Transactional
//    public Response save(@RestForm("firstName") String firstName, @RestForm("lastName") String lastName,
//                         @RestForm("tel") String tel, @RestForm("email") String email, @RestForm("lineId") String lineId) {
//        Client client = new Client();
//        client.setFirstName(firstName);
//        client.setLastName(lastName);
//        client.setEmail(email);
//        client.setLineId(lineId);
//        client.setTel(tel);
//        repo.persist(client);
//
//        JsonObject responseJson = Json.createObjectBuilder()
//                .add("redirect", "/client")
//                .build();
//
//        return Response.ok(responseJson.toString())
//                .header("HX-Redirect", "/client")
//                .build();
//    }

    @POST
    @Path("client/save")
    @Transactional
    public void save(@BeanParam ClientDto clientDto) {
        repo.persist(mapper.toEntity(clientDto));
    }

    @PUT
    @Path("client/update/{id}")
    @Transactional
    public TemplateInstance update(@PathParam("id") Long id, @RestForm("firstName") String firstName, @RestForm("lastName") String lastName,
                                 @RestForm("tel") String tel, @RestForm("email") String email, @RestForm("lineId") String lineId) {
        Client clientExist = repo.findById(id);
        clientExist.setFirstName(firstName);
        clientExist.setLastName(lastName);
        clientExist.setEmail(email);
        clientExist.setLineId(lineId);
        clientExist.setTel(tel);
        repo.persist(clientExist);

        return Templates.client$list(repo.listAll());
    }

    @GET
    @Path("client/modal/{id}")
    @Blocking
    public TemplateInstance modal(Long id) {
        Client byId = repo.findById(id);
        return Templates.modal(byId,"view");
    }
    @GET
    @Path("client/modal/edit/{id}")
    @Blocking
    public TemplateInstance modalEdit(Long id) {
        Client byId = repo.findById(id);
        return Templates.modal(byId,"edit");
    }

    @DELETE
    @Blocking
    @Path("client/{id}")
    public TemplateInstance delete(Long id) {
        repo.deleteById(id);
        return Templates.client$list(repo.listAll());
    }
}
