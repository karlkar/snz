package com.karol.sezonnazdrowie;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.prolificinteractive.materialcalendarview.CalendarDay;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.*;

/**
 * Created by Karol on 13.03.2016.
 */
public class FoodItem implements Parcelable, Comparable {

    private static final String TAG = "FoodItem";

    private String mName;
    private CalendarDay mStartDay1 = null;
    private CalendarDay mEndDay1 = null;
    private CalendarDay mStartDay2 = null;
    private CalendarDay mEndDay2 = null;

    private String mImageResourceId;
    private String mDesc;
    private String mLink;
    private boolean mIsFruit;

    private String mWater;
    private String mEnergy;
    private String mProtein;
    private String mFat;
    private String mCarbohydrates;
    private String mFiber;
    private String mSugars;

    private String mCalcium;
    private String mIron;
    private String mMagnesium;
    private String mPhosphorus;
    private String mPotassium;
    private String mSodium;
    private String mZinc;

    private String mVitC;
    private String mThiamin;
    private String mRiboflavin;
    private String mNiacin;
    private String mVitB6;
    private String mFolate;
    private String mVitA;
    private String mVitE;
    private String mVitK;

    private boolean mEnabled = false;
    private boolean mIsSubItem = false;

    private ArrayList<FoodItem> mSubItems = null;

    public FoodItem(String[] row, boolean isFruit) {
        if (!row[0].isEmpty())
            mName = row[0];
        else {
            mIsSubItem = true;
            mName = row[1];
        }
        mImageResourceId = row[2];
        String startDate1 = row[3];
        String endDate1 = row[4];
        String startDate2 = row[5];
        String endDate2 = row[6];
        mDesc = row[7];
        mLink = row[8];
        mIsFruit = isFruit;
        mWater = row[9];
        mEnergy = row[10];
        mProtein = row[11];
        mFat = row[12];
        mCarbohydrates = row[13];
        mFiber = row[14];
        mSugars = row[15];
        mCalcium = row[16];
        mIron = row[17];
        mMagnesium = row[18];
        mPhosphorus = row[19];
        mPotassium = row[20];
        mSodium = row[21];
        mZinc = row[22];
        mVitC = row[23];
        mThiamin = row[24];
        mRiboflavin = row[25];
        mNiacin = row[26];
        mVitB6 = row[27];
        mFolate = row[28];
        mVitA = row[29];
        mVitE = row[30];
        mVitK = row[31];

        SimpleDateFormat format = new SimpleDateFormat("d.MM");
        try {
            if (!startDate1.isEmpty() && !endDate1.isEmpty() && !startDate1.equals("-")) {
                mStartDay1 = CalendarDay.from(format.parse(startDate1));
                mEndDay1 = CalendarDay.from(format.parse(endDate1));
            }
            if (!startDate2.isEmpty() && !endDate2.isEmpty() && !startDate2.equals("-")) {
                mStartDay2 = CalendarDay.from(format.parse(startDate2));
                mEndDay2 = CalendarDay.from(format.parse(endDate2));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    protected FoodItem(Parcel in) {
        mName = in.readString();
        mStartDay1 = (CalendarDay) in.readValue(CalendarDay.class.getClassLoader());
        mEndDay1 = (CalendarDay) in.readValue(CalendarDay.class.getClassLoader());
        mStartDay2 = (CalendarDay) in.readValue(CalendarDay.class.getClassLoader());
        mEndDay2 = (CalendarDay) in.readValue(CalendarDay.class.getClassLoader());
        mImageResourceId = in.readString();
        mDesc = in.readString();
        mLink = in.readString();
        mIsFruit = in.readByte() != 0x00;
        mWater = in.readString();
        mEnergy = in.readString();
        mProtein = in.readString();
        mFat = in.readString();
        mCarbohydrates = in.readString();
        mFiber = in.readString();
        mSugars = in.readString();
        mCalcium = in.readString();
        mIron = in.readString();
        mMagnesium = in.readString();
        mPhosphorus = in.readString();
        mPotassium = in.readString();
        mSodium = in.readString();
        mZinc = in.readString();
        mVitC = in.readString();
        mThiamin = in.readString();
        mRiboflavin = in.readString();
        mNiacin = in.readString();
        mVitB6 = in.readString();
        mFolate = in.readString();
        mVitA = in.readString();
        mVitE = in.readString();
        mVitK = in.readString();
        mIsSubItem = in.readByte() != 0x00;
        mSubItems = in.readArrayList(FoodItem.class.getClassLoader());
        if (in.readByte() == 0x01) {
            mSubItems = new ArrayList<FoodItem>();
            in.readList(mSubItems, FoodItem.class.getClassLoader());
        } else {
            mSubItems = null;
        }
    }

    public static final Creator<FoodItem> CREATOR = new Creator<FoodItem>() {
        @Override
        public FoodItem createFromParcel(Parcel in) {
            return new FoodItem(in);
        }

        @Override
        public FoodItem[] newArray(int size) {
            return new FoodItem[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mName);
        dest.writeValue(mStartDay1);
        dest.writeValue(mEndDay1);
        dest.writeValue(mStartDay2);
        dest.writeValue(mEndDay2);
        dest.writeString(mImageResourceId);
        dest.writeString(mDesc);
        dest.writeString(mLink);
        dest.writeByte((byte) (mIsFruit ? 0x01 : 0x00));
        dest.writeString(mWater);
        dest.writeString(mEnergy);
        dest.writeString(mProtein);
        dest.writeString(mFat);
        dest.writeString(mCarbohydrates);
        dest.writeString(mFiber);
        dest.writeString(mSugars);
        dest.writeString(mCalcium);
        dest.writeString(mIron);
        dest.writeString(mMagnesium);
        dest.writeString(mPhosphorus);
        dest.writeString(mPotassium);
        dest.writeString(mSodium);
        dest.writeString(mZinc);
        dest.writeString(mVitC);
        dest.writeString(mThiamin);
        dest.writeString(mRiboflavin);
        dest.writeString(mNiacin);
        dest.writeString(mVitB6);
        dest.writeString(mFolate);
        dest.writeString(mVitA);
        dest.writeString(mVitE);
        dest.writeString(mVitK);
        dest.writeByte((byte) (mIsSubItem ? 0x01 : 0x00));
        if (mSubItems == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(mSubItems);
        }
    }

    public static ArrayList<FoodItem> createItems(Context context, int resId, boolean isFruit) {
        ArrayList<FoodItem> items = new ArrayList<>();
        InputStream in = context.getResources().openRawResource(resId);
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            String line;
            int cnt = 0;
            FoodItem lastItem = null;
            while ((line = reader.readLine()) != null) {
                if (cnt++ < 2)
                    continue;
                String[] rowData = line.split("#", -1);
                FoodItem curItem = new FoodItem(rowData, isFruit);
                if (curItem.isSubItem() && lastItem != null && !lastItem.isSubItem())
                    lastItem.addSubItem(curItem);
                else {
                    items.add(curItem);
                    lastItem = curItem;
                }
            }
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
        finally {
            try {
                if (in != null)
                    in.close();
            }
            catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        Collections.sort(items);
        return items;
    }

    private void addSubItem(FoodItem item) {
        if (mSubItems == null)
            mSubItems = new ArrayList<>();
        mSubItems.add(item);
    }

    public boolean existsAt(CalendarDay date) {
        if (mStartDay1 == null || mEndDay1 == null) // całoroczne
            return true;
        if (mStartDay1.getMonth() < date.getMonth() && mEndDay1.getMonth() > date.getMonth())
            return true;
        if ((mStartDay1.getMonth() == date.getMonth() && mStartDay1.getDay() <= date.getDay())
                || (mEndDay1.getMonth() == date.getMonth() && mEndDay1.getDay() >= date.getDay()))
            return true;
        if (mEndDay1.isBefore(mStartDay1)) {
            if (date.getMonth() > mStartDay1.getMonth() || date.getMonth() < mEndDay1.getMonth())
                return true;
            if ((date.getMonth() == mStartDay1.getMonth() && date.getDay() >= date.getDay())
                    || (date.getMonth() == mEndDay1.getMonth() && date.getDay() <= mEndDay1.getDay()))
                return true;
        }
        if (mStartDay2 != null && mEndDay2 != null) {
            if (mStartDay2.getMonth() < date.getMonth() && mEndDay2.getMonth() > date.getMonth())
                return true;
            if ((mStartDay2.getMonth() == date.getMonth() && mStartDay2.getDay() <= date.getDay())
                    || (mEndDay2.getMonth() == date.getMonth()) && mEndDay2.getDay() >= date.getDay())
                return true;
            if (mEndDay2.isBefore(mStartDay2)) {
                if (date.getMonth() > mStartDay2.getMonth() || date.getMonth() < mEndDay2.getMonth())
                    return true;
                if ((date.getMonth() == mStartDay2.getMonth() && date.getDay() >= date.getDay())
                        || (date.getMonth() == mEndDay2.getMonth() && date.getDay() <= mEndDay2.getDay()))
                    return true;
            }
        }
        return false;
    }

    public String getName() {
        return mName;
    }
	
	public String getConjugatedName() {
		return mName.toLowerCase();
	}

    public String getImage() {
        return mImageResourceId;
    }

    public String getDesc() {
        return mDesc;
    }

    public String getLink() {
        return mLink;
    }

    public boolean isFruit() {
        return mIsFruit;
    }

    public String getWater() {
        return mWater;
    }

    public String getEnergy() {
        return mEnergy;
    }

    public String getProtein() {
        return mProtein;
    }

    public String getFat() {
        return mFat;
    }

    public String getCarbohydrates() {
        return mCarbohydrates;
    }

    public String getFiber() {
        return mFiber;
    }

    public String getSugars() {
        return mSugars;
    }

    public String getCalcium() {
        return mCalcium;
    }

    public String getIron() {
        return mIron;
    }

    public String getMagnesium() {
        return mMagnesium;
    }

    public String getPhosphorus() {
        return mPhosphorus;
    }

    public String getPotassium() {
        return mPotassium;
    }

    public String getSodium() {
        return mSodium;
    }

    public String getZinc() {
        return mZinc;
    }

    public String getVitC() {
        return mVitC;
    }

    public String getThiamin() {
        return mThiamin;
    }

    public String getRiboflavin() {
        return mRiboflavin;
    }

    public String getNiacin() {
        return mNiacin;
    }

    public String getVitB6() {
        return mVitB6;
    }

    public String getFolate() {
        return mFolate;
    }

    public String getVitA() {
        return mVitA;
    }

    public String getVitE() {
        return mVitE;
    }

    public String getVitK() {
        return mVitK;
    }

    public boolean isEnabled() {
        return mEnabled;
    }

    public void setEnabled(boolean enabled) {
        mEnabled = enabled;
    }

    public boolean isSubItem() {
        return mIsSubItem;
    }

    @Override
    public String toString() {
        return mName;
    }

    @Override
    public int compareTo(Object another) {
        if (!(another instanceof FoodItem))
            return 1;
        FoodItem other = (FoodItem) another;
        return getName().compareTo(other.getName());
    }

    public boolean hasProximates() {
        return (mWater != null && !mWater.isEmpty()) || (mEnergy != null && !mEnergy.isEmpty()) || (mProtein != null && !mProtein.isEmpty())
                || (mFat != null && !mFat.isEmpty()) || (mCarbohydrates != null && !mCarbohydrates.isEmpty()) || (mFiber != null && !mFiber.isEmpty())
                || (mSugars != null && !mSugars.isEmpty());
    }

    public boolean hasMinerals() {
        return (mCarbohydrates != null && !mCarbohydrates.isEmpty()) || (mIron != null && !mIron.isEmpty()) || (mMagnesium != null &&!mMagnesium.isEmpty())
                || (mPhosphorus != null && !mPhosphorus.isEmpty()) || (mPotassium != null && !mPotassium.isEmpty()) || (mSodium != null && !mSodium.isEmpty())
                || (mZinc != null && !mZinc.isEmpty());
    }

    public boolean hasVitamins() {
        return (mVitC != null && !mVitC.isEmpty()) || (mThiamin != null && !mThiamin.isEmpty()) || (mRiboflavin != null && !mRiboflavin.isEmpty())
                || (mNiacin != null && !mNiacin.isEmpty()) || (mVitB6 != null && !mVitB6.isEmpty()) || (mFolate != null && !mFolate.isEmpty())
                || (mVitA != null && !mVitA.isEmpty()) || (mVitE != null && !mVitE.isEmpty()) || (mVitK != null && !mVitK.isEmpty());
    }

    public CalendarDay getStartDay1() {
        return mStartDay1;
    }

    public CalendarDay getEndDay1() {
        return mEndDay1;
    }

    public CalendarDay getStartDay2() {
        return mStartDay2;
    }

    public CalendarDay getEndDay2() {
        return mEndDay2;
    }

    public CalendarDay getNearestSeasonDay(CalendarDay rel) {
        if (mStartDay1 == null)
            return rel;

        if (existsAt(rel))
            return rel;

        return getNearestSeasonStart(rel);
    }

	public CalendarDay getNearestSeasonStart(CalendarDay rel) {
        if (mStartDay1 == null)
            return null;
        int relInDays = rel.getMonth() * 30 + rel.getDay();
        int start1InDays = mStartDay1.getMonth() * 30 + mStartDay1.getDay();

        CalendarDay retVal1;
        if (start1InDays >= relInDays)
            retVal1 = CalendarDay.from(rel.getYear(), mStartDay1.getMonth(), mStartDay1.getDay());
        else
            retVal1 = CalendarDay.from(rel.getYear() + 1, mStartDay1.getMonth(), mStartDay1.getDay());

        if (mStartDay2 == null)
            return retVal1;

        int start2InDays = mStartDay2.getMonth() * 30 + mStartDay2.getDay();
        CalendarDay retVal2;
        if (start2InDays >= relInDays)
            retVal2 = CalendarDay.from(rel.getYear(), mStartDay2.getMonth(), mStartDay2.getDay());
        else
            retVal2 = CalendarDay.from(rel.getYear() + 1, mStartDay2.getMonth(), mStartDay2.getDay());

        int start1Diff = start1InDays - relInDays;
        int start2Diff = start2InDays - relInDays;

        if (start1Diff >= 0 && start2Diff >= 0) {
            if (start1Diff < start2Diff)
                return retVal1;
            else
                return retVal2;
        } else if (start1Diff < 0 && start2Diff >= 0)
            return retVal2;
        else
            return CalendarDay.from(rel.getYear() + 1, mStartDay1.getMonth(), mStartDay1.getDay());
	}
	
	public CalendarDay getNearestSeasonEnd(CalendarDay rel) {
        if (mEndDay1 == null)
            return null;
        int relInDays = rel.getMonth() * 30 + rel.getDay();
        int end1InDays = mEndDay1.getMonth() * 30 + mEndDay1.getDay();

        CalendarDay retVal1;
        if (end1InDays >= relInDays)
            retVal1 = CalendarDay.from(rel.getYear(), mEndDay1.getMonth(), mEndDay1.getDay());
        else
            retVal1 = CalendarDay.from(rel.getYear() + 1, mEndDay1.getMonth(), mEndDay1.getDay());

        if (mEndDay2 == null)
            return retVal1;

        int end2InDays = mEndDay2.getMonth() * 30 + mEndDay2.getDay();
        CalendarDay retVal2;
        if (end2InDays >= relInDays)
            retVal2 = CalendarDay.from(rel.getYear(), mEndDay2.getMonth(), mEndDay2.getDay());
        else
            retVal2 = CalendarDay.from(rel.getYear() + 1, mEndDay2.getMonth(), mEndDay2.getDay());

        int start1Diff = end1InDays - relInDays;
        int start2Diff = end2InDays - relInDays;

        if (start1Diff >= 0 && start2Diff >= 0) {
            if (start1Diff < start2Diff)
                return retVal1;
            else
                return retVal2;
        } else if (start1Diff < 0 && start2Diff >= 0)
            return retVal2;
        else
            return CalendarDay.from(rel.getYear() + 1, mEndDay2.getMonth(), mEndDay2.getDay());
	}
}
