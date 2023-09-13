package com.example.chi_tieu_giadinh.utiliti;

import java.util.HashMap;

public class Constraints {
    //hằng số toàn cục có thể truy cập từ bất kỳ đâu trong chương trình và giá trị của nó không thể thay đổi sau khi được gán
    // final là một giá trị không thay đổi và chỉ có thể được gán một lần.UsersAuth

    public static final String KEY_COLLECTIONS_VIDEO_STRORY="VideoStrory";
    public static final String KEY_STORY_VIDEO="videoUrl";
    public static final String KEY_STORY_DESCRPIT="decription";


    public static final String KEY_IS_USERS="isUser";
    public static final String KEY_COLLECTIONS_ADDTIN_TRANGCN="AddTinTrangCN";
    public static final String KEY_COLLECTIONS_SUKIEN="EventProfile";
    public static final String KEY_COLLECTIONS_EVENT="Event";
    public static final String KEY_COLLECTIONS_EVENT_USES="EventUsers";
    public static final String KEY_COLLECTIONS_EVENT_IMG="img";

    public static final String KEY_COLLECTIONS_EVENT_NAME="name";
    public static final String KEY_EVENT_NAME="name";
    public static final String KEY_EVENT_NGAY="ngay";
    public static final String KEY_EVENT_TIME="time";
    public static final String KEY_EVENT_NOIDUNG="noidung";
    public static final String KEY_EVENT_GHICHU="ghichu";
    public static final String KEY_IS_ADMIN="isAdmin";
    public static final String KEY_USES_AUTH="UsersAuth";
    public static final String KEY_COLLECTIONS_CHECK_TIN="DuyetTin";
    public static final String KEY_CHECK_IMG="DuyetImg";
    public static final String KEY_CHECK_NAME="DuyetName";
    public static final String KEY_CHECK_DES="DuyetDes";
    public static final String KEY_CHECK_ANH_DANG="DuyetImgDang";
    public static final String KEY_CHECK_VIDEO="DuyetVideo";
    public static final String KEY_COLLECTIONS_INCOME="IncomeData";
    public static final String KEY_COLLECTIONS_EXPENSIVE="ExpenseData";
    public static final String KEY_COLLECTIONS_LOCATION="Locations";
    public static final String KEY_LOCATION_ADDRESS="address";
    public static final String KEY_LOCATION_LATITIUE="latitude";
    public static final String KEY_LOCATION_LONGTITUDE="longitude";
    public static final String KEY_KHOANGKHAC_IMG="img";
    public static final String KEY_KHOANGKHAC_CAPTION="cap";
    public static final String KEY_COLLECTIONS_KHOANGKHAC="Storage";
    public static final String KEY_CHUCVU="chucVu";
    public static final String KEY_NOIO="noiO";
    public static final String KEY_NGAYTHANGNAM="ngaySinh";
    public static final String KEY_COUNT_NOTICE="countNotice";
    public static final String KEY_NOTICE="notice";
    public static final String KEY_COLECTION_NOTICE="noticeColecton";
    public static final String KEY_AVALIABLE="avaliablelity";
    public static final String KEY_COLECTION_CHAT="chat";
    public static final String KEY_MESSAGE="message";
    public static final String KEY_SENDER_IMGE="senderImage";
    public static final String KEY_SENDER_NAME="senderName";
    public static final String KEY_RECEIVE_NAME="receiveName";
    public static final String KEY_TIMESTAMP="timestamp";
    public static final String KEY_RECEIVE_IMGE="receiveImage";
    public static final String KEY_LAST_MESSAGE="lastMessage";
    public static final String KEY_USER="user";
    public static final String KEY_SENDER_ID="senderId";
    public static final String KEY_RECEIVED_ID="receivedId";
    public static final String KEY_COLECTION_CONVERSATIONS="convaersations";
    public static final String KEY_COLECTION_USERS="Users";
    public static final String KEY_NAME="name";
    public static final String KEY_EMAIL="email";
    public static final String KEY_ADD_EMAIL="email";
    public static final String KEY_DONGHO="lineFrience";
    public static final String KEY_GIOITINH="gioiTinh";
    public static final String KEY_PASSWORD="password";
    public static final String KEY_REFERENCE_NAME="chatAppPreference";
    public static final String KEY_IS_SIGN_IN="isSignedIn";
    public static final String KEY_USER_ID="userID";
    public static final String KEY_IMGE="image";
    public static final String KEY_FCM_TOKEN="fcmToken";
    public static final String KEY_IMGE_ANHNEN="imageAnhNen";

    public static final String KEY_COLECTION_ADD_TIN="addTin";
    public static final String KEY_COLECTION_ADD_IMAGE="addImage";
    public static final String KEY_COLECTION_ADD_IMAGE_DAI_DIEN="addImageDaiDien";
    public static final String KEY_COLECTION_ADD_NAME="addNam";
    public static final String KEY_COLECTION_ADD_EDITTEXT="addEditext";
    public static final String KEY_COLECTION_ADD_VIDEO="addVideo";
    public static final String KEY_COLECTION_ADD_COUNT="count_like";
    public static final String KEY_COLECTION_ICON_CHANGE="changeIcon";
    public static final String KEY_COLECTION_TIME="time";


    public static final String KEY_COLECTION_ADD_COMMENT="addComment";
    public static final String KEY_COLECTION_ADD_COMMENT_NAME="cmName";
    public static final String KEY_COLECTION_ADD_COMMENT_IMAGE="cmImage";
    public static final String KEY_COLECTION_ADD_COMMENT_DATE="cmDate";
    public static final String KEY_COLECTION_ADD_COMMENT_EDIT="cmEdit";

    public static final String REMOTE_MSG_INVITATION_REPONSE="invitationReponse";
    public static final String REMOTE_MSG_INVITATION_ACCEPTED="accepted";
    public static final String REMOTE_MSG_INVITATION_REJECTED="rejected";
    public static final String REMOTE_MSG_INVITATION_CANCEL="cancel";
    public static final String REMOTE_MSG_TYPE="type";
    public static final String REMOTE_MSG_INVITATION="invitation";
    public static final String REMOTE_MSG_MEETING_TYPE="meetingtype";
    public static final String REMOTE_MSG_INVITOR_TOKEN="invitertoken";
    public static final String REMOTE_MSG_DATA="data";
    public static final String REMOTE_MSG_REGISTATION_IDS="registaionids";

    public static final String REMOTE_MSG_AUTH="Authorization";
    public static final String REMOTE_MSG_CONTENT_TYPE="ContentType";

    public static HashMap<String,String> getRemoteMessageHeaders(){
        HashMap<String,String> headers=new HashMap<>();
        headers.put(
                Constraints.REMOTE_MSG_AUTH,"key=AAAAFl2nJ_8:APA91bHlma4ja1HLd4ocdFVZCxs3I_mo8dUaxhED5csf1OEQUZzXUo8IjXtyw9NlBFdnoHDPLpVFqBXSR3E-W3RP1-8FlD1i_e_pUYMnzEgAEp8i2gAcaaYHRU5s5-QQccOXtj3jlpta"
        );
        headers.put(Constraints.REMOTE_MSG_CONTENT_TYPE,"application/json");
        return headers;
    }
}
