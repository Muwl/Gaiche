package com.gaicheyunxiu.gaiche.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2016/5/8.
 */
public class ShopTypeEntity implements Serializable{
    public  String id;
    public  String name;
    public  String parentId;
    public  String image;
    public List<ShopTypeChildEntity> childEntities;
}
