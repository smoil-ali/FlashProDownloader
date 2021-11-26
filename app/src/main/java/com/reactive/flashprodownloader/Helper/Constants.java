package com.reactive.flashprodownloader.Helper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.reactive.flashprodownloader.R;


import java.io.File;

public class Constants {
    public static String FACEBOOK_SCRIPT = "javascript:function clickOnVideo(link, classValueName){" +
            "browser.getVideoData(link);" +
            "var element = document.getElementById(\"mInlineVideoPlayer\");" +
            "element.muted = true;" +
            "var parent = element.parentNode; " +
            "parent.removeChild(element);" +
            "parent.setAttribute('class', classValueName);}" +
            "function getVideoLink(){" +
            "try{var items = document.getElementsByTagName(\"div\");" +
            "for(i = 0; i < items.length; i++){" +
            "if(items[i].getAttribute(\"data-sigil\") == \"inlineVideo\"){" +
            "var classValueName = items[i].getAttribute(\"class\");" +
            "var jsonString = items[i].getAttribute(\"data-store\");" +
            "var obj = JSON && JSON.parse(jsonString) || $.parseJSON(jsonString);" +
            "var videoLink = obj.src;" +
            "var videoName = obj.videoID;" +
            "items[i].setAttribute('onclick', \"clickOnVideo('\"+videoLink+\"','\"+classValueName+\"')\");}}" +
            "var links = document.getElementsByTagName(\"a\");" +
            "for(i = 0; i < links.length; i++){" +
            "if(links[ i ].hasAttribute(\"data-store\")){" +
            "var jsonString = links[i].getAttribute(\"data-store\");" +
            "var obj = JSON && JSON.parse(jsonString) || $.parseJSON(jsonString);" +
            "var videoName = obj.videoID;" +
            "var videoLink = links[i].getAttribute(\"href\");" +
            "var res = videoLink.split(\"src=\");" +
            "var myLink = res[1];" +
            "links[i].parentNode.setAttribute('onclick', \"browser.getVideoData('\"+myLink+\"')\");" +
            "while (links[i].firstChild){" +
            "links[i].parentNode.insertBefore(links[i].firstChild," +
            "links[i]);}" +
            "links[i].parentNode.removeChild(links[i]);}}}catch(e){}}" +
            "getVideoLink();";

    public static final String FACEBOOK_SCRIPT2 = "javascript:function clickOnVideo(link, classValueName){" +
            "browser.getVideoData(link);" +
            "var element = document.getElementById(\"mInlineVideoPlayer\");" +
            "element.muted = true;" +
            "var parent = element.parentNode; " +
            "parent.removeChild(element);" +
            "parent.setAttribute('class', classValueName);}" +
            "function getVideoLink(){" +
            "try{var items = document.getElementsByTagName(\"div\");" +
            "for(i = 0; i < items.length; i++){" +
            "if(items[i].getAttribute(\"data-sigil\") == \"inlineVideo\"){" +
            "var classValueName = items[i].getAttribute(\"class\");" +
            "var jsonString = items[i].getAttribute(\"data-store\");" +
            "var obj = JSON && JSON.parse(jsonString) || $.parseJSON(jsonString);" +
            "var videoLink = obj.src;" +
            "var videoName = obj.videoID;" +
            "items[i].setAttribute('onclick', \"clickOnVideo('\"+videoLink+\"','\"+classValueName+\"')\");}}" +
            "var links = document.getElementsByTagName(\"a\");" +
            "for(i = 0; i < links.length; i++){" +
            "if(links[ i ].hasAttribute(\"data-store\")){" +
            "var jsonString = links[i].getAttribute(\"data-store\");" +
            "var obj = JSON && JSON.parse(jsonString) || $.parseJSON(jsonString);" +
            "var videoName = obj.videoID;" +
            "var videoLink = links[i].getAttribute(\"href\");" +
            "var res = videoLink.split(\"src=\");" +
            "var myLink = res[1];" +
            "links[i].parentNode.setAttribute('onclick', \"browser.getVideoData('\"+myLink+\"')\");" +
            "while (links[i].firstChild){" +
            "links[i].parentNode.insertBefore(links[i].firstChild," +
            "links[i]);}" +
            "links[i].parentNode.removeChild(links[i]);}}}catch(e){}}" +
            "getVideoLink();";
    public static final int TAB = 77;
    public static final String AUTHORITY = "com.reactive.flashprodownloader.fileProvider";
    public static final String PACKAGE_NAME = "com.reactive.FlashProDownloader";
    public static final String BASE_URL = "https://www.dailymotion.com/";
    public static final String JSON = "/json";
    public static final String TITLE = "?fields=title";
    public static final String HD = "stream_h264_sd_url";
    public static final String PARAMS = "PARAMS";
    public static final int HISTORY = 3;
    public static final int BOOKMARK = 4;
    public static final String URL = "https://www.google.com.pk/";
    public static final String DATABASE_NAME = "FLASH_PRO_DOWNLOADER";
    public static final String BLANK_URL = "about:blank";
    public static final String HOME_PAGE_TITLE = "HomePage";
    public static final String DAILYMOTION_URL = "https://www.dailymotion.com";
    public static final String DAILYMOTION_TITLE = "dailymotion";
    public static final String DATE = "DATE";
    public static final int  DAILYMOTION_ICON = R.drawable.dailymotion_icon;

    public static final String FACEBOOK_URL = "https://m.facebook.com";
    public static final String FACEBOOK_TITLE = "facebook";
    public static final int FACEBOOK_ICON = R.drawable.facebook_icon;

    public static final String GOOGLE_URL = "https://www.google.com.pk/";
    public static final String GOOGLE_TITLE = "google";
    public static final int GOOGLE_ICON = R.drawable.google_search_icon;



    public static final String TWITTER_URL = "https://twitter.com/?lang=en";
    public static final String TWITTER_TITLE = "twitter";
    public static final int TWITTER_ICON = R.drawable.twitter_icon;

    public static final String WIKI_URL = "https://www.wikipedia.org/";
    public static final String WIKI_TITLE = "wiki";
    public static final int  WIKI_ICON = R.drawable.wiki_icon;

    public static final String CRICBUZZ_URL = "https://www.cricbuzz.com/";
    public static final String CRICBUZZ_TITLE = "crickbuzz";
    public static final int CRICBUZZ_ICON = R.drawable.crickbuzz_icon;

    public static final String ALIEXPRESS_URL = "https://www.aliexpress.com/";
    public static final String ALIEXPRESS_TITLE = "AliExpress";
    public static final int ALIEXPRESS_ICON = R.drawable.aliexpress_icon;

    public static final String ESPN_URL = "https://www.espncricinfo.com/";
    public static final String ESPN_TITLE = "ESPN";
    public static final int ESPN_ICON  = R.drawable.espn_icon;

    public static final String LIKEE_URL = "https://likee.video/m_index?lang=en";
    public static final String LIKEE_TITLE = "Likee";
    public static final int LIKEE_ICON = R.drawable.likee_icon;

    public static final String TED_URL = "https://www.ted.com/";
    public static final String TED_TITLE = "ted";
    public static final int TED_ICON = R.drawable.ted_icon;

    public static final String INSTAGRAM_URL = "https://www.instagram.com/";
    public static final String INSTAGRAM_TITLE = "instagram";
    public static final int INSTAGRAM_ICON = R.drawable.instagram_icon;


    public static final String VIMEO_URL = "https://www.vimeo.com/watch/";
    public static final String VIMEO_TITLE = "vimeo";
    public static final int VIMEO_ICON = R.drawable.vimeo_icon;

    public static final String IMDB_URL = "https://www.imdb.com/";
    public static final String IMDB_TITLE = "imdb";
    public static final int IMDB_ICON = R.drawable.imdb_icon;


    public static String getHomePageScreenShot(Context context,String folderName){
        Bitmap myBitmap = null;
        File file = new File(context.getExternalFilesDir(null)+
                "/"+"ScreenShot/"+folderName+"/screenShot.jpeg");
        return file.getAbsolutePath();
//        if(file.exists()){
//            myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
//        }
//        return myBitmap;
    }

    public static Animation loadAnimation(Context context){
        return AnimationUtils.loadAnimation(context,R.anim.shake);
    }
}
