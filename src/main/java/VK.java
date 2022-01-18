import com.vk.api.sdk.client.TransportClient;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import com.vk.api.sdk.objects.users.Fields;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class VK {

    private static VkApiClient client;
    private static UserActor actor;

    public static void parseVK(ArrayList<Student> students) {
        try {
            TransportClient transportClient = HttpTransportClient.getInstance();
            VK.client = new VkApiClient(transportClient);
            VK.actor = new UserActor(205817451, "c8e8a4230c2500fc1981d6ba8e6cd03f7dfe7e3229e60c7ee30244f23ddb7ef777623d569cbbad3fe7a5e");

            var group = VK.client.groups()
                    .getMembers(VK.actor)
                    .groupId("198188261")
                    .fields(new ArrayList<>(Arrays. asList(Fields.PERSONAL, Fields.CITY)))
                    .executeAsString();

            JSONObject members = new JSONObject(group);
            JSONArray users = members
                    .getJSONObject("response")
                    .getJSONArray("items");

            for (Student student : students) {
                for (int i = 0; i < users.length(); i++) {
                    JSONObject VKUser = users.getJSONObject(i);
                    String userName = VKUser.get("last_name") + " " + VKUser.get("first_name");
                    if (Objects.equals(student.getName(), userName)) {
                        student.foundInVK(VKUser.getInt("id"));
                        try {
                            String city = VKUser.getJSONObject("city").getString("title");
                            student.setCity(city);
                            System.out.printf("%s [%s], %s\n", student.getName(), student.getVkId(), city);
                        } catch (Exception ignored) {}
                    }
                }
            }
        } catch (Exception e) {
            System.out.printf("VK Parse Error: %s \n", e.getMessage());
        }
    }
}
