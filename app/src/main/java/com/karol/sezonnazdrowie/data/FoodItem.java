package com.karol.sezonnazdrowie.data;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.prolificinteractive.materialcalendarview.CalendarDay;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

@Entity
public class FoodItem implements Comparable<FoodItem> {

    private static final String TAG = "FoodItem";

    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("d.MM", Locale.getDefault());
    public static final SimpleDateFormat DATE_FORMAT_TEXT = new SimpleDateFormat("d MMMM", Locale.getDefault());

    public FoodItem() {
    }

    @PrimaryKey
    @NonNull
    private String mName;
    private String mConjugatedName;

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

    public FoodItem(String[] row, boolean isFruit) {
        mName = row[0];
        mConjugatedName = row[1];
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

        try {
            if (!startDate1.isEmpty() && !endDate1.isEmpty() && !startDate1.equals("-")) {
                mStartDay1 = CalendarDay.from(DATE_FORMAT.parse(startDate1));
                mEndDay1 = CalendarDay.from(DATE_FORMAT.parse(endDate1));
            }
            if (!startDate2.isEmpty() && !endDate2.isEmpty() && !startDate2.equals("-")) {
                mStartDay2 = CalendarDay.from(DATE_FORMAT.parse(startDate2));
                mEndDay2 = CalendarDay.from(DATE_FORMAT.parse(endDate2));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public FoodItem(
            boolean isFruit,
            String name,
            String subname,
            String file,
            String startDate1,
            String endDate1,
            String startDate2,
            String endDate2,
            String desc,
            String link,
            String Water,
            String Energy,
            String Protein,
            String fat,
            String Carbohydrate,
            String Fiber,
            String Sugars,
            String Calcium,
            String Iron,
            String Magnesium,
            String Phosphorus,
            String Potassium,
            String Sodium,
            String Zinc,
            String VitaminC,
            String Thiamin,
            String Riboflavin,
            String Niacin,
            String VitaminB6,
            String Folate,
            String VitaminA,
            String VitaminE,
            String VitaminK
    ) {
        mName = name;
        mConjugatedName = subname;
        mImageResourceId = file;

        try {
            if (!startDate1.isEmpty() && !endDate1.isEmpty() && !startDate1.equals("-")) {
                mStartDay1 = CalendarDay.from(DATE_FORMAT.parse(startDate1));
                mEndDay1 = CalendarDay.from(DATE_FORMAT.parse(endDate1));
            }
            if (!startDate2.isEmpty() && !endDate2.isEmpty() && !startDate2.equals("-")) {
                mStartDay2 = CalendarDay.from(DATE_FORMAT.parse(startDate2));
                mEndDay2 = CalendarDay.from(DATE_FORMAT.parse(endDate2));
            }
        } catch (ParseException e) {
            Log.e(TAG, "Failed to parse", e);
        }

        mDesc = desc;

        mLink = link;
        mWater = Water;
        mEnergy = Energy;
        mProtein = Protein;
        mFat = fat;
        mCarbohydrates = Carbohydrate;
        mFiber = Fiber;
        mSugars = Sugars;
        mCalcium = Calcium;
        mIron = Iron;
        mMagnesium = Magnesium;
        mPhosphorus = Phosphorus;
        mPotassium = Potassium;
        mSodium = Sodium;
        mZinc = Zinc;
        mVitC = VitaminC;
        mThiamin = Thiamin;
        mRiboflavin = Riboflavin;
        mNiacin = Niacin;
        mVitB6 = VitaminB6;
        mFolate = Folate;
        mVitA = VitaminA;
        mVitE = VitaminE;
        mVitK = VitaminK;

        mIsFruit = isFruit;
    }

    public boolean existsAt(CalendarDay date) {
        if (mStartDay1 == null || mEndDay1 == null) // całoroczne
            return true;
        Calendar cal = date.getCalendar();
        cal.set(Calendar.YEAR, 1970);
        long relDate = cal.getTimeInMillis();

        long start = mStartDay1.getCalendar().getTimeInMillis();
        long end = mEndDay1.getCalendar().getTimeInMillis();

        if (start <= end) {
            if (relDate >= start && relDate <= end) {
                return true;
            }
        } else {
            if (relDate <= end || relDate >= start) {
                return true;
            }
        }

        if (mStartDay2 != null && mEndDay2 != null) {
            start = mStartDay2.getCalendar().getTimeInMillis();
            end = mEndDay2.getCalendar().getTimeInMillis();

            if (start <= end) {
                return relDate >= start && relDate <= end;
            } else {
                return relDate <= end || relDate >= start;
            }
        }
        return false;
    }

    public String getName() {
        return mName;
    }

    public String getConjugatedName() {
        return mConjugatedName;
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

    public void setIsFruit(boolean isFruit) {
        mIsFruit = isFruit;
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

    public void setName(String name) {
        mName = name;
    }

    public void setConjugatedName(String conjugatedName) {
        mConjugatedName = conjugatedName;
    }

    public void setStartDay1(CalendarDay startDay1) {
        mStartDay1 = startDay1;
    }

    public void setEndDay1(CalendarDay endDay1) {
        mEndDay1 = endDay1;
    }

    public void setStartDay2(CalendarDay startDay2) {
        mStartDay2 = startDay2;
    }

    public void setEndDay2(CalendarDay endDay2) {
        mEndDay2 = endDay2;
    }

    public String getImageResourceId() {
        return mImageResourceId;
    }

    public void setImageResourceId(String imageResourceId) {
        mImageResourceId = imageResourceId;
    }

    public void setDesc(String desc) {
        mDesc = desc;
    }

    public void setLink(String link) {
        mLink = link;
    }

    public void setFruit(boolean fruit) {
        mIsFruit = fruit;
    }

    public void setWater(String water) {
        mWater = water;
    }

    public void setEnergy(String energy) {
        mEnergy = energy;
    }

    public void setProtein(String protein) {
        mProtein = protein;
    }

    public void setFat(String fat) {
        mFat = fat;
    }

    public void setCarbohydrates(String carbohydrates) {
        mCarbohydrates = carbohydrates;
    }

    public void setFiber(String fiber) {
        mFiber = fiber;
    }

    public void setSugars(String sugars) {
        mSugars = sugars;
    }

    public void setCalcium(String calcium) {
        mCalcium = calcium;
    }

    public void setIron(String iron) {
        mIron = iron;
    }

    public void setMagnesium(String magnesium) {
        mMagnesium = magnesium;
    }

    public void setPhosphorus(String phosphorus) {
        mPhosphorus = phosphorus;
    }

    public void setPotassium(String potassium) {
        mPotassium = potassium;
    }

    public void setSodium(String sodium) {
        mSodium = sodium;
    }

    public void setZinc(String zinc) {
        mZinc = zinc;
    }

    public void setVitC(String vitC) {
        mVitC = vitC;
    }

    public void setThiamin(String thiamin) {
        mThiamin = thiamin;
    }

    public void setRiboflavin(String riboflavin) {
        mRiboflavin = riboflavin;
    }

    public void setNiacin(String niacin) {
        mNiacin = niacin;
    }

    public void setVitB6(String vitB6) {
        mVitB6 = vitB6;
    }

    public void setFolate(String folate) {
        mFolate = folate;
    }

    public void setVitA(String vitA) {
        mVitA = vitA;
    }

    public void setVitE(String vitE) {
        mVitE = vitE;
    }

    public void setVitK(String vitK) {
        mVitK = vitK;
    }

    @Override
    public String toString() {
        return mName;
    }

    @Override
    public int compareTo(@NonNull FoodItem another) {
        return getName().compareTo(another.getName());
    }

    public boolean hasProximates() {
        return (mWater != null && !mWater.isEmpty()) || (mEnergy != null && !mEnergy.isEmpty()) || (mProtein != null && !mProtein.isEmpty())
                || (mFat != null && !mFat.isEmpty()) || (mCarbohydrates != null && !mCarbohydrates.isEmpty()) || (mFiber != null && !mFiber.isEmpty())
                || (mSugars != null && !mSugars.isEmpty());
    }

    public boolean hasMinerals() {
        return (mCarbohydrates != null && !mCarbohydrates.isEmpty()) || (mIron != null && !mIron.isEmpty()) || (mMagnesium != null && !mMagnesium.isEmpty())
                || (mPhosphorus != null && !mPhosphorus.isEmpty()) || (mPotassium != null && !mPotassium.isEmpty()) || (mSodium != null && !mSodium.isEmpty())
                || (mZinc != null && !mZinc.isEmpty());
    }

    public boolean hasVitamins() {
        return (mVitC != null && !mVitC.isEmpty()) || (mThiamin != null && !mThiamin.isEmpty()) || (mRiboflavin != null && !mRiboflavin.isEmpty())
                || (mNiacin != null && !mNiacin.isEmpty()) || (mVitB6 != null && !mVitB6.isEmpty()) || (mFolate != null && !mFolate.isEmpty())
                || (mVitA != null && !mVitA.isEmpty()) || (mVitE != null && !mVitE.isEmpty()) || (mVitK != null && !mVitK.isEmpty());
    }

    public String getNearestSeasonString() {
        CalendarDay today = CalendarDay.today();
        CalendarDay start = getNearestSeasonStart(today);
        if (start == null) {
            return "";
        }
        CalendarDay end = getNearestSeasonEnd(today);
        if (end == null) {
            return "";
        }
        String startDayStr = DATE_FORMAT_TEXT.format(start.getDate());
        String endDayStr = DATE_FORMAT_TEXT.format(end.getDate());
        return startDayStr + " - " + endDayStr;
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

    public CalendarDay getNearestSeasonDay(@NonNull CalendarDay rel) {
        if (mStartDay1 == null) {
            return rel;
        }

        if (existsAt(rel)) {
            return rel;
        }

        return getNearestSeasonStart(rel);
    }

    public CalendarDay getNearestSeasonStart(@NonNull CalendarDay rel) {
        if (mStartDay1 == null) {
            return null;
        }
        int relInDays = rel.getMonth() * 30 + rel.getDay();
        int start1InDays = mStartDay1.getMonth() * 30 + mStartDay1.getDay();

        CalendarDay retVal1;
        if (start1InDays >= relInDays) {
            retVal1 = CalendarDay.from(rel.getYear(), mStartDay1.getMonth(), mStartDay1.getDay());
        } else {
            retVal1 = CalendarDay.from(rel.getYear() + 1, mStartDay1.getMonth(), mStartDay1.getDay());
        }

        if (mStartDay2 == null) {
            return retVal1;
        }

        int start2InDays = mStartDay2.getMonth() * 30 + mStartDay2.getDay();
        CalendarDay retVal2;
        if (start2InDays >= relInDays) {
            retVal2 = CalendarDay.from(rel.getYear(), mStartDay2.getMonth(), mStartDay2.getDay());
        } else {
            retVal2 = CalendarDay.from(rel.getYear() + 1, mStartDay2.getMonth(), mStartDay2.getDay());
        }

        int start1Diff = start1InDays - relInDays;
        int start2Diff = start2InDays - relInDays;

        if (start1Diff >= 0 && start2Diff >= 0) {
            if (start1Diff < start2Diff) {
                return retVal1;
            } else {
                return retVal2;
            }
        } else if (start1Diff < 0 && start2Diff >= 0) {
            return retVal2;
        } else {
            return CalendarDay.from(rel.getYear() + 1, mStartDay1.getMonth(), mStartDay1.getDay());
        }
    }

    public CalendarDay getNearestSeasonEnd(@NonNull CalendarDay rel) {
        if (mEndDay1 == null) {
            return null;
        }
        int relInDays = rel.getMonth() * 30 + rel.getDay();
        int end1InDays = mEndDay1.getMonth() * 30 + mEndDay1.getDay();

        CalendarDay retVal1;
        if (end1InDays >= relInDays) {
            retVal1 = CalendarDay.from(rel.getYear(), mEndDay1.getMonth(), mEndDay1.getDay());
        } else {
            retVal1 = CalendarDay.from(rel.getYear() + 1, mEndDay1.getMonth(), mEndDay1.getDay());
        }

        if (mEndDay2 == null) {
            return retVal1;
        }

        int end2InDays = mEndDay2.getMonth() * 30 + mEndDay2.getDay();
        CalendarDay retVal2;
        if (end2InDays >= relInDays) {
            retVal2 = CalendarDay.from(rel.getYear(), mEndDay2.getMonth(), mEndDay2.getDay());
        } else {
            retVal2 = CalendarDay.from(rel.getYear() + 1, mEndDay2.getMonth(), mEndDay2.getDay());
        }

        int start1Diff = end1InDays - relInDays;
        int start2Diff = end2InDays - relInDays;

        if (start1Diff >= 0 && start2Diff >= 0) {
            if (start1Diff < start2Diff) {
                return retVal1;
            } else {
                return retVal2;
            }
        } else if (start1Diff < 0 && start2Diff >= 0) {
            return retVal2;
        } else {
            return CalendarDay.from(rel.getYear() + 1, mEndDay1.getMonth(), mEndDay1.getDay());
        }
    }
}
