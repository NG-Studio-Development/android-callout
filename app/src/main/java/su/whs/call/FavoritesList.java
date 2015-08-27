package su.whs.call;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;

import su.whs.call.models.UserInfo;

public class FavoritesList {
	private static final String PREFS_FILE = "favorites.dat";
	private static FavoritesList mInstance = null;
	private SharedPreferences mPrefs = null;
	
	private FavoritesList(Context context) {
		mPrefs = context.getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE);
	}
	
	public static synchronized FavoritesList getInstance(Context context) {
		if (mInstance==null)
			mInstance = new FavoritesList(context);
		return mInstance;
	}
	
	@SuppressWarnings("unchecked")
	public synchronized ArrayList<UserInfo> getUsers() {
		ArrayList<UserInfo> result = new ArrayList<UserInfo>();
		try {
			 result = (ArrayList<UserInfo>)fromString(mPrefs.getString("favorites", ""));
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
	
	public synchronized void addToFavorites(UserInfo user) {
		ArrayList<UserInfo> result = getUsers();
		result.add(user);
		SharedPreferences.Editor e = mPrefs.edit();
		try {
			e.putString("favorites", toString(result));
			e.commit();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
	}
	
	/** Read the object from Base64 string. */
	   private static Object fromString( String s ) throws IOException ,
	                                                       ClassNotFoundException {
	        byte [] data = Base64.decode(s,Base64.DEFAULT);
	        ObjectInputStream ois = new ObjectInputStream( 
	                                        new ByteArrayInputStream(  data ) );
	        Object o  = ois.readObject();
	        ois.close();
	        return o;
	   }

	    /** Write the object to a Base64 string. */
	    private static String toString( Serializable o ) throws IOException {
	        ByteArrayOutputStream baos = new ByteArrayOutputStream();
	        ObjectOutputStream oos = new ObjectOutputStream( baos );
	        oos.writeObject( o );
	        oos.close();
	        return new String( Base64.encode( baos.toByteArray(), Base64.DEFAULT ) );
	    }
}
