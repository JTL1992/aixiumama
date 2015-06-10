package com.harmazing.aixiumama.utils;

import android.content.Context;

import com.harmazing.aixiumama.filter.InkwellFilter;
import com.harmazing.aixiumama.filter.AmaroFilter;
import com.harmazing.aixiumama.filter.BrannanFilter;
import com.harmazing.aixiumama.filter.Cute1977Filter;
import com.harmazing.aixiumama.filter.EarlybirdFilter;
import com.harmazing.aixiumama.filter.HefeFilter;
import com.harmazing.aixiumama.filter.HudsonFilter;
import com.harmazing.aixiumama.filter.LomoFilter;
import com.harmazing.aixiumama.filter.LordKelvinFilter;
import com.harmazing.aixiumama.filter.NashvilleFilter;
import com.harmazing.aixiumama.filter.RiseFilter;
import com.harmazing.aixiumama.filter.SierraFilter;
import com.harmazing.aixiumama.filter.SutroFilter;
import com.harmazing.aixiumama.filter.ToasterFilter;
import com.harmazing.aixiumama.filter.ValenciaFilter;
import com.harmazing.aixiumama.filter.WaldenFilter;
import com.harmazing.aixiumama.filter.XprollFilter;

import java.util.ArrayList;
import java.util.List;

import jp.co.cyberagent.android.gpuimage.GPUImageFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageToneCurveFilter;

/**
 * Created by guoyongwei on 2014/12/29.
 */
public class GPUImageUtil {


    public static List<? extends GPUImageFilter> getImageFilter(Context mContext) {

        List<GPUImageFilter> gPUImageList = new ArrayList<GPUImageFilter>();

        GPUImageToneCurveFilter gpuImageToneCurveFilter = new GPUImageToneCurveFilter();
        gPUImageList.add(gpuImageToneCurveFilter);
        gPUImageList.add(new EarlybirdFilter(mContext));
        gPUImageList.add(new NashvilleFilter(mContext));
        gPUImageList.add(new WaldenFilter(mContext));
        gPUImageList.add(new HudsonFilter(mContext));
        gPUImageList.add(new SierraFilter(mContext));
        gPUImageList.add(new ValenciaFilter(mContext));
        gPUImageList.add(new RiseFilter(mContext));
        gPUImageList.add(new XprollFilter(mContext));
        gPUImageList.add(new AmaroFilter(mContext));
        gPUImageList.add(new LomoFilter(mContext));
        gPUImageList.add(new SutroFilter(mContext));
        gPUImageList.add(new BrannanFilter(mContext));
        gPUImageList.add(new HefeFilter(mContext));
        gPUImageList.add(new LordKelvinFilter(mContext));
        gPUImageList.add(new Cute1977Filter(mContext));
        gPUImageList.add(new ToasterFilter(mContext));
        gPUImageList.add(new InkwellFilter(mContext));

        return gPUImageList;
    }
}
