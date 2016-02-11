package com.yookos.nsconsumer.services;

import com.datastax.driver.mapping.Mapper;
import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.Result;
import com.google.android.gcm.server.Sender;
import com.google.gson.Gson;
import com.yookos.nsconsumer.models.NotificationEvent;
import com.yookos.nsconsumer.models.NotificationUser;
import com.yookos.nsconsumer.models.YookoreUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by bekezelam on 04/07/2015.
 * www.yookos.com
 */

@Component
public class PushAndroid {

    public final String GROUP_INVITE = "GROUP_INVITE";
    public final String VIDEO_NOTIFICATION = "VIDEO_NOTIFICATION";
    public final String AUDIO_NOTIFICATION = "AUDIO_NOTIFICATION";
    public final String BLOG_POST_CREATED = "BLOG_POST_CREATED";
    public final String STATUS_UPDATE_CREATED = "STATUS_UPDATE_CREATED";
    public final String PHOTO_CREATED = "PHOTO_CREATED";
    public final String COMMENT_NOTIFICATION = "COMMENT_NOTIFICATION";
    public final String USER_VERIFICATION = "String USER_VERIFICATION";
    public final String RESET_PASSWORD = "RESET_PASSWORD";
    public final String CHANGE_PASSWORD = "CHANGE_PASSWORD";
    public final String UPDATE_PRIMARY_EMAIL = "UPDATE_PRIMARY_EMAIL";
    public final String FRIEND_REQUEST = "FRIEND_REQUEST";
    public final String FRIEND_REQUEST_ACCEPTED = "FRIEND_REQUEST_ACCEPTED";

    @Autowired
    Gson gson;

    @Autowired
    Mapper<NotificationUser> mapper;

    @Autowired
    Environment environment;

    private Logger LOG = LoggerFactory.getLogger(PushAndroid.class);

    public List<Result> sendAndroidPushNotification(NotificationEvent notification) {
        List<Result> results = new ArrayList<>();
        try {
            Message message = buildPushMessage(notification);
            Sender sender = new Sender(environment.getProperty("google.server.key"));
            if (notification.getRecipient().getUsername().equalsIgnoreCase("jomski2009")
                    || notification.getRecipient().getUsername().equalsIgnoreCase("phumi1")
                    || notification.getRecipient().getUsername().equalsIgnoreCase("freidaegbuna")
                    || notification.getRecipient().getUsername().equalsIgnoreCase("Alpharogers")
                    || notification.getRecipient().getUsername().equalsIgnoreCase("Tomisin_fashina")
                    || notification.getRecipient().getUsername().equalsIgnoreCase("ritariso")
                    ) {
                LOG.info("Special user: {}", notification.getRecipient().getUsername());
//                Thread.sleep(10000L);

            }

            //Cut back from here later
            List<String> regIds = getRegIdsFor(notification.getRecipient());
            if (regIds != null && regIds.size() > 0) {
                for (String pushRegistrationId : regIds) {
                    try {
                        if (null == pushRegistrationId || pushRegistrationId.trim().equals("")) {
                            //fail gracefully, please.
                            LOG.error(":: invalid registration_id - empty string");
                            throw new IllegalArgumentException("invalid registration ID");
                        }

                        if ((notification.getActor().getUsername() != null) &&
                                notification.getActor().getUsername().toLowerCase().equals("pastorchrislive")) {
                            LOG.info("sending PastorChrisLive push to [{}]: {}", notification.getRecipient().getUsername(), pushRegistrationId);
                        } else {
                            LOG.info(String.format("sending push [%s]: %s", notification.getRecipient().getUsername(), pushRegistrationId));
                        }

                        results.add(sender.send(message, pushRegistrationId, 1));

                    } catch (Exception exception) {
                        LOG.error(exception.getMessage());
                    }

                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
        return results;
    }

    private List<String> getRegIdsFor(YookoreUser recipient) {
        NotificationUser notificationUser = mapper.get(UUID.fromString(recipient.getUserId()));
        if (notificationUser != null) {
            LOG.info("Notification recipient: {}", notificationUser.getUsername());
            return notificationUser.getRegistration_ids();
        } else {
            return new ArrayList<>();
        }
    }

    private Message buildPushMessage(NotificationEvent notification) throws IOException {
        Message.Builder builder = new Message.Builder();
        builder.addData("type", notification.getAction());
        builder.addData("actor", notification.getActor().getUserId());
        String payload = gson.toJson(notification);
        builder.addData("payload", payload);
        if (notification.getActor().getFullNames() == null && notification.getActor().getFullNames().trim().length() == 0) {
            builder.addData("fullNames", notification.getActor().getFirstName() + " " + notification.getActor().getLastName());
        } else {
            builder.addData("fullNames", notification.getActor().getFullNames());
        }
        addExtraInfo(notification, builder);
        LOG.info("Message data: {}", builder.build().getData());
        return builder.build();
    }

    private void addExtraInfo(NotificationEvent notification, Message.Builder builder) {

        if (notification.getAction().equals(FRIEND_REQUEST_ACCEPTED)) {
            Object actorAvatarUrl = notification.getExtraInfo().get("actorImgurl");
            builder.addData("actorAvatarUrl", actorAvatarUrl == null ? "" : actorAvatarUrl.toString());
            Object actorProfileUrl = notification.getExtraInfo().get("actorProfileUrl");
            builder.addData("actorProfileUrl", actorProfileUrl == null ? "" : actorProfileUrl.toString());
        }

        if (notification.getAction().equals(AUDIO_NOTIFICATION)
                || notification.getAction().equals(VIDEO_NOTIFICATION)
                || notification.getAction().equals(BLOG_POST_CREATED)
                || notification.getAction().equals(COMMENT_NOTIFICATION)
                || notification.getAction().equals(STATUS_UPDATE_CREATED)) {
            Object status_actorAvatarUrl = notification.getExtraInfo().get("actorImgurl");
            Object status_id = notification.getExtraInfo().get("objectid");
            builder.addData("actorAvatarUrl", status_actorAvatarUrl == null ? "" : status_actorAvatarUrl.toString());
            builder.addData("objectid", status_id == null ? "" : status_id.toString());
        }

        if (notification.getAction().equals(PHOTO_CREATED)) {
            Object photo_actorAvatarUrl = notification.getExtraInfo().get("actorImgurl");
            builder.addData("actorAvatarUrl", photo_actorAvatarUrl == null ? "" : photo_actorAvatarUrl.toString());
            Object photo_id = notification.getExtraInfo().get("objectid");

            // photoUrl - multiple
            try {
                if (isMultiPhoto(notification)) {
                    Object photoUrl = ((ArrayList) notification.getExtraInfo().get("list_image_url")).get(0);
                    builder.addData("photo", photoUrl == null ? "" : photoUrl.toString());
                } else {
                    // photoUrl - single
                    Object photoUrl = ((ArrayList) notification.getExtraInfo().get("list_image_url")).get(0);
                    builder.addData("photo", photoUrl == null ? "" : photoUrl.toString());
                }

                builder.addData("objectid", photo_id == null ? "" : photo_id.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

//        switch (action) {
//            case USER_VERIFICATION:
//                break;
//            case RESET_PASSWORD:
//                break;
//            case CHANGE_PASSWORD:
//                break;
//            case UPDATE_PRIMARY_EMAIL:
//                break;
//            case GROUP_INVITE:
//            case FRIEND_REQUEST:
//            case FRIEND_REQUEST_ACCEPTED:
//                Object actorAvatarUrl = notification.getExtraInfo().get("actorImgurl");
//                builder.addData("actorAvatarUrl", actorAvatarUrl == null ? "" : actorAvatarUrl.toString());
//                Object actorProfileUrl = notification.getExtraInfo().get("actorProfileUrl");
//                builder.addData("actorProfileUrl", actorProfileUrl == null ? "" : actorProfileUrl.toString());
//                break;
//            case ACTIVITY_STREAM_COMMENT:
//                break;
//            case ACTIVITY_STREAM_POST:
//                break;
//            case AUDIO_NOTIFICATION:
//            case VIDEO_NOTIFICATION:
//            case BLOG_POST_CREATED:
//            case STATUS_UPDATE_CREATED:
//                // actorAvatar
//                Object status_actorAvatarUrl = notification.getExtraInfo().get("actorImgurl");
//                Object status_id = notification.getExtraInfo().get("objectid");
//                builder.addData("actorAvatarUrl", status_actorAvatarUrl == null ? "" : status_actorAvatarUrl.toString());
//                builder.addData("objectid", status_id == null ? "" : status_id.toString());
//
//                break;
//            case PHOTO_CREATED:
//                // actorAvatar
//                Object photo_actorAvatarUrl = notification.getExtraInfo().get("actorImgurl");
//                builder.addData("actorAvatarUrl", photo_actorAvatarUrl == null ? "" : photo_actorAvatarUrl.toString());
//                Object photo_id = notification.getExtraInfo().get("objectid");
//
//                // photoUrl - multiple
//                try {
//                    if (TargetUserUtils.isMultiPhoto(notification)) {
//                        Object photoUrl = ((ArrayList) notification.getExtraInfo().get("list_image_url")).get(0);
//                        builder.addData("photo", photoUrl == null ? "" : photoUrl.toString());
//                    } else {
//                        // photoUrl - single
//                        Object photoUrl = ((ArrayList) notification.getExtraInfo().get("list_image_url")).get(0);
//                        builder.addData("photo", photoUrl == null ? "" : photoUrl.toString());
//                    }
//
//                    builder.addData("objectid", photo_id == null ? "" : photo_id.toString());
//                } catch (NullPointerException e) {
//                    e.printStackTrace();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                break;
//            case GROUP_CREATED:
//                break;
//        }
    }

    private boolean isMultiPhoto(NotificationEvent notification) {
        try {
            ArrayList<Object> photolist = (ArrayList) notification.getExtraInfo().get("list_image_url");

            if (null == photolist || photolist.isEmpty())
                return false;

            if (photolist.size() > 1)
                return true;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;

    }
}