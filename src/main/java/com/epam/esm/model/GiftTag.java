package com.epam.esm.model;

public class GiftTag {
    int gift_id;
    int tag_id;

    GiftTag(){}

    public GiftTag(int gift_id, int tag_id){
        this.gift_id = gift_id;
        this.tag_id = tag_id;
    }

    public int getGift_id() {
        return gift_id;
    }

    public void setGift_id(int gift_id) {
        this.gift_id = gift_id;
    }

    public int getTag_id() {
        return tag_id;
    }

    public void setTag_id(int tag_id) {
        this.tag_id = tag_id;
    }
}
