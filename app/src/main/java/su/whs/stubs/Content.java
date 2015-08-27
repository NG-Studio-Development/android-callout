package su.whs.stubs;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Base64;

public class Content {
	public static JSONObject DISTANCE;
	public static JSONObject USERINFO;
	
	static String b64users = "eyJlcnJvcl9jb2RlIjowLCJkYXRhIjpbeyJ1c2VyX2luZm8iOnsiYXZhdGFyIjp7InVybCI6Ii91" +
			
"cGxvYWRzL3VzZXIvYXZhdGFyLzEvMThfMDFfMDEuanBnIn0sImNhbGxzX2NvdW50IjoxLCJjaXR5"+ 
"X2lkIjpudWxsLCJjcmVhdGVkX2F0IjoiMjAxMy0xMC0xNFQwNzo0ODo1N1oiLCJkZXNjcmlwdGlv"+
"biI6ImdzYXBvcGhpc0BtYWlsLnJ1IiwiZW1haWwiOiJnc2Fwb3BoaXNAbWFpbC5ydSIsImZiX2lk"+
"IjpudWxsLCJnX2lkIjpudWxsLCJpZCI6MSwibmFtZSI6ImdzYXBvcGhpc0BtYWlsLnJ1IiwicHJp"+
"Y2UiOm51bGwsInN0YXRlIjoi0LjRgdC/0L7Qu9C90LjRgtC10LvRjCIsInRlbGVwaG9uZV9udW1i"+
"ZXIiOiI0MjQzNDAwIiwidXBkYXRlZF9hdCI6IjIwMTMtMTAtMTdUMDk6MjI6NDRaIiwidmtfaWQi"+
"Om51bGwsInlfaWQiOm51bGx9LCJmYXZvcml0ZXMiOltdLCJhdmVyYWdlX3JhdGUiOiI1LjAiLCJy"+
"ZXZpZXdzIjpbeyJkZXNjcmlwdGlvbiI6Ik90bGk0bm8iLCJzdWJfY2F0ZWdvcnlfaWQiOm51bGws"+
"InVzZXJfaWQiOiIxIn1dfSx7InVzZXJfaW5mbyI6eyJhdmF0YXIiOnsidXJsIjpudWxsfSwiY2Fs"+
"bHNfY291bnQiOjAsImNpdHlfaWQiOm51bGwsImNyZWF0ZWRfYXQiOiIyMDE0LTAxLTA5VDExOjQ4"+
"OjI3WiIsImRlc2NyaXB0aW9uIjoieWFyaWtAeWFyaWsucnUiLCJlbWFpbCI6InlhcmlrQHlhcmlr"+
"LnJ1IiwiZmJfaWQiOm51bGwsImdfaWQiOm51bGwsImlkIjoyLCJuYW1lIjoieWFyaWtAeWFyaWsu"+
"cnUiLCJwcmljZSI6bnVsbCwic3RhdGUiOiLQuNGB0L/QvtC70L3QuNGC0LXQu9GMIiwidGVsZXBo"+
"b25lX251bWJlciI6InlhcmlrQHlhcmlrLnJ1IiwidXBkYXRlZF9hdCI6IjIwMTQtMDEtMDlUMTE6"+
"NDg6MjdaIiwidmtfaWQiOm51bGwsInlfaWQiOm51bGx9LCJmYXZvcml0ZXMiOltdLCJhdmVyYWdl"+
"X3JhdGUiOiIwIiwicmV2aWV3cyI6W119LHsidXNlcl9pbmZvIjp7ImF2YXRhciI6eyJ1cmwiOm51"+
"bGx9LCJjYWxsc19jb3VudCI6MCwiY2l0eV9pZCI6bnVsbCwiY3JlYXRlZF9hdCI6IjIwMTQtMDMt"+
"MTNUMTI6NTg6MzNaIiwiZGVzY3JpcHRpb24iOm51bGwsImVtYWlsIjoidHJpb2RpbnBAZ21haWwu"+
"Y29tIiwiZmJfaWQiOm51bGwsImdfaWQiOm51bGwsImlkIjozLCJuYW1lIjoibHVmZnkiLCJwcmlj"+
"ZSI6bnVsbCwic3RhdGUiOiLQv9C+0LvRjNC30L7QstCw0YLQtdC70YwiLCJ0ZWxlcGhvbmVfbnVt"+
"YmVyIjpudWxsLCJ1cGRhdGVkX2F0IjoiMjAxNC0wMy0xM1QxMjo1ODozM1oiLCJ2a19pZCI6bnVs"+
"bCwieV9pZCI6bnVsbH0sImZhdm9yaXRlcyI6W10sImF2ZXJhZ2VfcmF0ZSI6IjAiLCJyZXZpZXdz"+
"IjpbXX1dfQo=";
	static {
		try {
			DISTANCE=new JSONObject("{\"country_id\":1,\"created_at\":\"2013-10-14T07:49:50Z\",\"enable\":false,\"id\":1,\"lattitude\":\"55.45\",\"longitude\":\"37.46\",\"name_en\":\"Moskow\",\"name_ru\":\"\u041c\u043e\u0441\u043a\u0432\u0430\",\"range\":null,\"updated_at\":\"2013-10-14T07:49:50Z\"}");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			USERINFO=new JSONObject(new String(Base64.decode(b64users, Base64.DEFAULT)));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
