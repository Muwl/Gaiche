package com.gaicheyunxiu.gaiche.activity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.gaicheyunxiu.gaiche.R;
import com.gaicheyunxiu.gaiche.adapter.PartsAdapter;
import com.gaicheyunxiu.gaiche.model.CommodityEntity;
import com.gaicheyunxiu.gaiche.model.CommodityState;
import com.gaicheyunxiu.gaiche.model.ReturnState;
import com.gaicheyunxiu.gaiche.utils.Constant;
import com.gaicheyunxiu.gaiche.utils.LogManager;
import com.gaicheyunxiu.gaiche.utils.ToastUtils;
import com.gaicheyunxiu.gaiche.utils.ToosUtils;
import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/3/14.
 * 配件列表页面
 */
public class ShopListActivity extends BaseActivity implements View.OnClickListener{

    private ImageView back;

    private TextView title;

    private TextView car;

    private View carLin;

    private RadioGroup group;

    private RadioButton defaultrb;

    private RadioButton pricerb;

    private RadioButton volume;

    private TextView brand;

    private PullToRefreshListView listView;

    private boolean proFlag = true;

    private int pageNo = 1;

    private PartsAdapter adapter;

    private String id;

    private View pro;

    private String brandName;

    private List<CommodityEntity> commodityEntityList;

    private String sort;

    private int comeFlag;//1 代表广告 2 代表热门列表

    private String type;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_part);
        initView();
    }

    private void initView() {
        comeFlag= getIntent().getIntExtra("comeFlag", 0);
        if (comeFlag==1){
            id=getIntent().getStringExtra("id");
        }else if(comeFlag==2){
            type=getIntent().getStringExtra("type");
        }

        commodityEntityList=new ArrayList<>();
        back= (ImageView) findViewById(R.id.title_back);
        title= (TextView) findViewById(R.id.title_text);
        car= (TextView) findViewById(R.id.part_carbrand);
        carLin=findViewById(R.id.part_carlin);
        group= (RadioGroup) findViewById(R.id.part_rb);
        defaultrb= (RadioButton) findViewById(R.id.part_default);
        pricerb= (RadioButton) findViewById(R.id.part_moods);
        volume= (RadioButton) findViewById(R.id.part_technology);
        brand= (TextView) findViewById(R.id.part_price);
        listView= (PullToRefreshListView) findViewById(R.id.part_lisview);
        pro=  findViewById(R.id.part_pro);
        back.setOnClickListener(this);
        adapter=new PartsAdapter(this,commodityEntityList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ShopListActivity.this, ShopDetailActivity.class);
                startActivity(intent);
            }
        });

        group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.part_default) {
                    sort = "0";
                    getAdShop(1);
                }
            }
        });
        pricerb.setOnClickListener(this);
        volume.setOnClickListener(this);
        brand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ShopListActivity.this, BrandActivity.class);
                if (comeFlag==1){
                    intent.putExtra("comeFlag",1);
                    intent.putExtra("id", id);
                }else if (comeFlag==2){
                    intent.putExtra("type",type);
                    intent.putExtra("comeFlag",2);
                }

                if (!ToosUtils.isStringEmpty(brandName)) {
                    intent.putExtra("name", brandName);
                }
                startActivityForResult(intent, 1116);
            }
        });
        group.check(R.id.part_default);

        title.setText("商品列表");

        listView.setMode(PullToRefreshBase.Mode.BOTH);
        listView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                closePro();
                if (refreshView.getCurrentMode().equals(PullToRefreshBase.Mode.PULL_FROM_START)) {
                    getAdShop(1);
                } else if (refreshView.getCurrentMode().equals(PullToRefreshBase.Mode.PULL_FROM_END)) {
                    getAdShop(pageNo + 1);
                }

            }

        });
        sort="0";
//        getAdShop(1);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode!=RESULT_OK){
            return;
        }
        if (requestCode==1116){
            brandName=data.getStringExtra("brandName");
            getAdShop(1);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.title_back:
                finish();
                break;

            case R.id.part_technology:
                if ("11".equals(sort)){
                    sort="12";
                    Drawable drawable = getResources().getDrawable(R.mipmap.sort_nor);
                    drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());//必须设置图片大小，否则不显示
                    pricerb.setCompoundDrawables(null, null, drawable, null);

                    Drawable drawable2 = getResources().getDrawable(R.mipmap.sort_down);
                    drawable2.setBounds(0, 0, drawable2.getMinimumWidth(), drawable2.getMinimumHeight());//必须设置图片大小，否则不显示
                    volume.setCompoundDrawables(null, null, drawable2, null);

                }else{
                    sort="11";
                    Drawable drawable = getResources().getDrawable(R.mipmap.sort_nor);
                    drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());//必须设置图片大小，否则不显示
                    pricerb.setCompoundDrawables(null, null, drawable, null);

                    Drawable drawable2 = getResources().getDrawable(R.mipmap.sort_top);
                    drawable2.setBounds(0, 0, drawable2.getMinimumWidth(), drawable2.getMinimumHeight());//必须设置图片大小，否则不显示
                    volume.setCompoundDrawables(null, null, drawable2, null);
                }
                getAdShop(1);
                break;

            case R.id.part_moods:
                if ("21".equals(sort)) {
                    sort = "22";
                    Drawable drawable = getResources().getDrawable(R.mipmap.sort_nor);
                    drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());//必须设置图片大小，否则不显示
                    volume.setCompoundDrawables(null, null, drawable, null);

                    Drawable drawable2 = getResources().getDrawable(R.mipmap.sort_down);
                    drawable2.setBounds(0, 0, drawable2.getMinimumWidth(), drawable2.getMinimumHeight());//必须设置图片大小，否则不显示
                    pricerb.setCompoundDrawables(null, null, drawable2, null);
                }else {
                    sort = "21";

                    Drawable drawable = getResources().getDrawable(R.mipmap.sort_nor);
                    drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());//必须设置图片大小，否则不显示
                    volume.setCompoundDrawables(null, null, drawable, null);

                    Drawable drawable2 = getResources().getDrawable(R.mipmap.sort_top);
                    drawable2.setBounds(0, 0, drawable2.getMinimumWidth(), drawable2.getMinimumHeight());//必须设置图片大小，否则不显示
                    pricerb.setCompoundDrawables(null, null, drawable2, null);
                }
                getAdShop(1);
                break;
        }
    }

    private void openPro(){
        proFlag=true;
    }

    private void closePro(){
        proFlag=false;
    }



    /**
     * 获取广告商品
     */
    private void getAdShop(final int page ) {
        RequestParams rp = new RequestParams();
        HttpUtils utils = new HttpUtils();
        utils.configTimeout(20000);
        rp.addBodyParameter("sort", sort);
        if (ToosUtils.isStringEmpty(brandName)){
            rp.addBodyParameter("brand","全部");
        }else{
            rp.addBodyParameter("brand",brandName);
        }
        rp.addBodyParameter("pageNo", pageNo + "");
        String url="advertisement/detail";

        if (comeFlag==1){
            rp.addBodyParameter("id", id);
            url="advertisement/detail";
        }else if (comeFlag==2){
            rp.addBodyParameter("type", type);
            rp.addBodyParameter("carTypeId", "");
            url="popularProject/findPopProjCommodity";
        }


        utils.send(HttpRequest.HttpMethod.POST, Constant.ROOT_PATH
                + url, rp, new RequestCallBack<String>() {
            @Override
            public void onStart() {
                super.onStart();
                if (proFlag) {
                    pro.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(HttpException arg0, String arg1) {
                ToastUtils.displayFailureToast(ShopListActivity.this);
                pro.setVisibility(View.GONE);
                listView.onRefreshComplete();
            }

            @Override
            public void onSuccess(ResponseInfo<String> arg0) {
                pro.setVisibility(View.GONE);
                listView.onRefreshComplete();
                try {
                    Gson gson = new Gson();
                    ReturnState state = gson.fromJson(arg0.result,
                            ReturnState.class);
                    if (Constant.RETURN_OK.equals(state.msg)) {
                        LogManager.LogShow("-----", arg0.result,
                                LogManager.ERROR);
                        CommodityState commodityState = gson.fromJson(arg0.result, CommodityState.class);
                        pageNo = Integer.valueOf(page);
                        if (pageNo == 1) {
                            commodityEntityList.clear();
                            adapter.notifyDataSetChanged();
                        }
                        if (commodityState.result != null && commodityState.result.size() > 0) {
                            for (int i = 0; i < commodityState.result.size(); i++) {
                                commodityEntityList.add(commodityState.result.get(i));
                            }
                            adapter.notifyDataSetChanged();
                        }

                    } else if (Constant.TOKEN_ERR.equals(state.msg)) {
                        ToastUtils.displayShortToast(ShopListActivity.this,
                                "验证错误，请重新登录");
                        ToosUtils.goReLogin(ShopListActivity.this);
                    } else {
                        ToastUtils.displayShortToast(ShopListActivity.this,
                                (String) state.result);
                    }
                } catch (Exception e) {
                    ToastUtils.displaySendFailureToast(ShopListActivity.this);
                }

            }
        });
    }



}