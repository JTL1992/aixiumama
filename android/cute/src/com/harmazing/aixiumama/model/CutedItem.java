package com.harmazing.aixiumama.model;

/**
 * Created by Administrator on 2014/12/8.
 */
public class CutedItem {
 private String []userName;
 private String []ImageUrl;
 private String []description;
 private String []IconUrl;
 private String  []date;
 private int []cuteId;
 private int []userId;
 private String [] title;
    public CutedItem (String []userName,String []description,String []ImageUrl,String []IconUrl,String[] date,int[] cuteId,int[] userId,String []title){
        this.userName = userName;
        this.description = description;
        this.IconUrl = IconUrl;
        this.ImageUrl = ImageUrl;
        this.date = date;
        this.cuteId = cuteId;
        this.userId = userId;
        this.title = title;
    }
//    public CutedItem getCutedItem(int start,int end){
//        if (size() > start && size() > end){
//             String [] userName = new String[]{};
//             String []ImageUrl= new String[]{};
//             String []description= new String[]{};
//             String []IconUrl= new String[]{};
//             String  []date= new String[]{};
//             int []cuteId= new int[]{};
//             int []userId= new int[]{};
//                userName[0] = this.userName[start];
//                ImageUrl[0] = this.ImageUrl[start];
//                description[0] = this.description[start];
//                IconUrl[0] = this.IconUrl[start];
//                date[0] = this.date[start];
//                cuteId[0] = this.cuteId[start];
//                userId[0] = this.cuteId[start];
//                userName[1] = this.userName[end];
//                ImageUrl[1] = this.ImageUrl[end];
//                description[1] = this.description[end];
//                IconUrl[1] = this.IconUrl[end];
//                date[1] = this.date[end];
//                cuteId[1] = this.cuteId[end];
//                userId[1] = this.cuteId[end];
//
//            return new CutedItem(userName,description,ImageUrl,IconUrl,date,cuteId,userId);
//        }
//        else
//            return null;
//    }
    public int size(){
        return this.userName.length;
    }
    public int[] getCuteId() {
        return cuteId;
    }

    public int[] getUserId() {
        return userId;
    }

    public String[] getDescription() {
        return description;
    }

    public String[] getIconUrl() {
        return IconUrl;
    }

    public String[] getImageUrl() {
        return ImageUrl;
    }

    public String[] getUserName() {
        return userName;
    }

    public String[] getDate() {
        return date;
    }

    public String[] getTitle() {
        return title;
    }
}
