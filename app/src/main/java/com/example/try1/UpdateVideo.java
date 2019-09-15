package com.example.try1;

public class UpdateVideo {
    String adId;
    String type;
    String url;
    int playNum;
    public UpdateVideo (String adId,String type,String url,int playNum){
        this.adId = adId;
        this.type = type;
        this.url = url;
        this.playNum = playNum;
    }
    private String getAdId(){
        return adId;
    }
    private String getType(){
        return type;
    }
    private String getUrl(){
        return url;
    }
    private int getPlayNum(){
        return playNum;
    }
}
