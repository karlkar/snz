package com.karol.sezonnazdrowie.data;

import android.content.Context;
import android.preference.PreferenceManager;

import com.karol.sezonnazdrowie.R;
import com.karol.sezonnazdrowie.model.SnzAlarmManager;
import com.prolificinteractive.materialcalendarview.CalendarDay;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import androidx.room.RoomDatabase;

@androidx.room.Database(entities = {FoodItem.class}, exportSchema = false, version = 1)
public abstract class Database extends RoomDatabase {

    private static final long INCOMING_SEASON_DAYS_DIFF = 32;

    private ArrayList<FoodItem> mCurrentFruits;
    private ArrayList<FoodItem> mCurrentVegetables;

    protected abstract FoodItemDao getFoodItemDao();

    public ArrayList<FoodItem> getCurrentFruits() {
        if (mCurrentFruits != null) {
            return mCurrentFruits;
        }

        List<FoodItem> allFruits = getAllFruits();

        CalendarDay today = CalendarDay.today();
        mCurrentFruits = new ArrayList<>();
        for (FoodItem item : allFruits) {
            if (item.existsAt(today)) {
                mCurrentFruits.add(item);
            }
        }
        return mCurrentFruits;
    }

    public ArrayList<FoodItem> getCurrentVegetables() {
        if (mCurrentVegetables != null) {
            return mCurrentVegetables;
        }

        List<FoodItem> allVegetables = getAllVegetables();

        CalendarDay today = CalendarDay.today();
        mCurrentVegetables = new ArrayList<>();
        for (FoodItem item : allVegetables) {
            if (item.existsAt(today)) {
                mCurrentVegetables.add(item);
            }
        }

        return mCurrentVegetables;
    }

    public ArrayList<FoodItem> getIncomingItems() {
        ArrayList<FoodItem> list = new ArrayList<>();
        CalendarDay today = CalendarDay.today();
        for (FoodItem item : getAllFruits()) {
            if (!item.existsAt(today)) {
                CalendarDay startDay1 = item.getStartDay1();

                long daysDiff = startDay1.getCalendar().get(Calendar.DAY_OF_YEAR) - today.getCalendar().get(Calendar.DAY_OF_YEAR);
                if (daysDiff >= 0 && daysDiff < INCOMING_SEASON_DAYS_DIFF) {
                    list.add(item);
                } else {
                    CalendarDay startDay2 = item.getStartDay2();
                    if (startDay2 != null) {
                        daysDiff = startDay2.getCalendar().get(Calendar.DAY_OF_YEAR) - today.getCalendar().get(Calendar.DAY_OF_YEAR);
                        if (daysDiff >= 0 && daysDiff < INCOMING_SEASON_DAYS_DIFF) {
                            list.add(item);
                        }
                    }
                }
            }
        }
        for (FoodItem item : getAllVegetables()) {
            if (!item.existsAt(today)) {
                CalendarDay startDay1 = item.getStartDay1();

                long daysDiff = startDay1.getCalendar().get(Calendar.DAY_OF_YEAR) - today.getCalendar().get(Calendar.DAY_OF_YEAR);
                if (daysDiff >= 0 && daysDiff < INCOMING_SEASON_DAYS_DIFF) {
                    list.add(item);
                } else {
                    CalendarDay startDay2 = item.getStartDay2();
                    if (startDay2 != null) {
                        daysDiff = startDay2.getCalendar().get(Calendar.DAY_OF_YEAR) - today.getCalendar().get(Calendar.DAY_OF_YEAR);
                        if (daysDiff >= 0 && daysDiff < INCOMING_SEASON_DAYS_DIFF) {
                            list.add(item);
                        }
                    }
                }
            }
        }

        Collections.sort(list, new Comparator<FoodItem>() {
			@Override
			public int compare(FoodItem lhs, FoodItem rhs) {
        	    CalendarDay today = CalendarDay.today();
        	    CalendarDay lhsDay = lhs.getNearestSeasonStart(today);
        	    CalendarDay rhsDay = rhs.getNearestSeasonStart(today);
				if (lhsDay == null && rhsDay != null) {
                    return -1;
                }
				if (lhsDay != null && rhsDay == null) {
                    return 1;
                }
				if (lhsDay == null && rhsDay == null || lhsDay.equals(rhsDay)) {
                    return 0;
                }
				return lhsDay.isBefore(rhsDay) ? -1 : 1;
			}
		});
        return list;
    }

//    public void loadData(Context ctx) {
    //TODO: startSetAlarmsTask
//        mFruits = FoodItem.createItems(ctx, com.karol.sezonnazdrowie.R.raw.fruits, true);
//        mVegetables = FoodItem.createItems(ctx, R.raw.vegetables, false);
//
//		boolean alarmsSet = PreferenceManager.getDefaultSharedPreferences(ctx).getBoolean("pref_alarms_set", false);
//		if (!alarmsSet) {
//            SnzAlarmManager.startSetAlarmsTask(ctx, this);
//        }
//    }

    public List<FoodItem> getAllFruits() {
        return getFoodItemDao().getAllFruits();
    }

    public List<FoodItem> getAllVegetables() {
        return getFoodItemDao().getAllVegetables();
    }

    public FoodItem getItem(String itemName) {
        return getFoodItemDao().getItem(itemName);
    }

    public void populate() {
        FoodItem items[] = new FoodItem[]{
                new FoodItem("AGREST", "agrest", "agrest", "1.05", "31.08", "", "", "Agrest jest bogaty w błonnik pokarmowy oraz kwasy: jabłkowy i cytrynowy, które wspomagają trawienie. Ponadto zawiera znaczne ilości witaminy C oraz wapń, żelazo, magnez, fosfor i potas.", "https://ndb.nal.usda.gov/ndb/foods/show/2219?fgcd=&manu=&lfacet=&format=&count=&max=35&offset=&sort=&qlookup=gooseberries", "87.87 g", "44 kcal", "0.88 g", "0.58 g", "10.18 g", "4.3 g", "", "25 mg", "0.31 mg", "10 mg", "27 mg", "198 mg", "1 mg", "0.12 mg", "27.7 mg", "0.040 mg", "0.030 mg", "0.300 mg", "0.080 mg", "6 µg", "15 µg", "0.37 mg", ""),
                new FoodItem("ARBUZ", "arbuza", "arbuz1", "15.07", "31.08", "", "", "Arbuz doskonale nawadnia, ponad 90% masy całego owocu stanowi woda. Ponadto zawiera dużo witamin i minerałów, głównie witaminę A, magnez, fosfor, potas. Z plastrem 300 g (plaster 5 cm, arbuz o średnicy 30 cm) dostarczymy organizmowi 84 µg witaminy A, to już 1/10 dziennego zapotrzebowania, a kto poprzestanie na jednym plastrze?", "https://ndb.nal.usda.gov/ndb/foods/show/2393?fgcd=&manu=&lfacet=&format=&count=&max=35&offset=&sort=&qlookup=Watermelon", "91.45 g", "30 kcal", "0.61 g", "0.15 g", "7.55 g", "0.4 g", "6.20 g", "7 mg", "0.24 mg", "10 mg", "11 mg", "112 mg", "1 mg", "0.10 mg", "8.1 mg", "0.033 mg", "0.021 mg", "0.178 mg", "0.045 mg", "3 µg", "28 µg", "0.05 mg", "0.1 µg"),
                new FoodItem("ARONIA", "aronię", "aronia", "1.08", "30.09", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""),
                new FoodItem("BERBERYS", "berberysa", "berberys", "1.08", "31.10", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""),
                new FoodItem("BORÓWKA AMERYKAŃSKA", "borówkę amerykańską", "borowka", "15.07", "30.09", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""),
                new FoodItem("BRZOSKWINIA", "brzoskwinię", "brzoskwinia1", "1.07", "30.09", "", "", "", "https://ndb.nal.usda.gov/ndb/foods/show/2311?fgcd=&manu=&lfacet=&format=&count=&max=35&offset=&sort=&qlookup=peach", "88.87 g", "39 kcal", "0.91 g", "0.25 g", "9.54 g", "1.5 g", "8.39 g", "6 mg", "0.25 mg", "9 mg", "20 mg", "190 mg", "", "0.17 mg", "6.6 mg", "0.024 mg", "0.031 mg", "0.806 mg", "0.025 mg", "4 µg", "16 µg", "0.73 mg", "2.6 µg"),
                new FoodItem("BRUSZNICA BORÓWKA", "brusznicę borówkę", "brusznica_borowka", "1.08", "31.10", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""),
                new FoodItem("CZARNY BEZ", "czarny bez", "czarny_bez", "15.08", "15.10", "", "", "", "https://ndb.nal.usda.gov/ndb/foods/show/2200?manu=&fgcd=", "79.80 g", "73 kcal", "0.66 g", "0.50 g", "18.40 g", "7.0 g", "", "38 mg", "1.60 mg", "5 mg", "39 mg", "280 mg", "6 mg", "0.11 mg", "36.0 mg", "0.070 mg", "0.060 mg", "0.500 mg", "0.230 mg", "6 µg", "30 µg", "", ""),
                new FoodItem("CZEREŚNIE", "czereśnie", "czeresnia1", "15.06", "15.08", "", "", "Czereśnie są bogatym źródłem antyoksydantów, które chronią nas przed wolnymi rodnikami, kwasów owocowych, witamin i soli mineralnych. Są także dobrym źródłem jodu, nie ustępują w tym morskim rybom. Polecane są szczególnie osobom, którym doskwiera dna moczanowa, ponieważ spożywanie czereśni zmniejsza ilość kwasu moczowego we krwi. ", "https://ndb.nal.usda.gov/ndb/foods/show/2183?fgcd=&manu=&lfacet=&format=&count=&max=35&offset=&sort=&qlookup=cherries", "82.25 g", "63 kcal", "1.06 g", "0.20 g", "16.01 g", "2.1 g", "12.82 g", "13 mg", "0.36 mg", "11 mg", "21 mg", "222 mg", "", "0.07 mg", "7.0 mg", "0.027 mg", "0.033 mg", "0.154 mg", "0.049 mg", "4 µg", "3 µg", "0.07 mg", "2.1 µg"),
                new FoodItem("DEREŃ", "dereń", "deren", "15.08", "30.09", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""),
                new FoodItem("GŁÓG", "głóg", "glog", "15.08", "31.10", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""),
                new FoodItem("JAGODA CZARNA", "czarną jagodę", "jagody2", "1.06", "31.08", "", "", "Jagody bogate są głównie w błonnik i antyoksydanty. Dzięki dużej zawartości garbników uszczelniają błony śluzowe żołądka, neutralizują szkodliwe produkty przemiany materii i spowalniają ruchy robaczkowe jelit. Sok z jagód wspomaga organizm podczas zatrucia pokarmowego, gdyż wychwytuje z organizmu wszelkie toksyny.", "https://ndb.nal.usda.gov/ndb/foods/show/2166?fgcd=&manu=&lfacet=&format=&count=&max=35&offset=&sort=&qlookup=blueberries", "84.21 g", "57 kcal", "0.74 g", "0.33 g", "14.49 g", "2.4 g", "9.96 g", "6 mg", "0.28 mg", "6 mg", "12 mg", "77 mg", "1 mg", "0.16 mg", "9.7 mg", "0.037 mg", "0.041 mg", "0.418 mg", "0.052 mg", "6 µg", "3 µg", "0.57 mg", "19.3 µg"),
                new FoodItem("JEŻYNY", "jeżyny", "jezyna2", "1.07", "31.08", "", "", "", "https://ndb.nal.usda.gov/ndb/foods/show/2161?fgcd=&manu=&lfacet=&format=&count=&max=35&offset=&sort=&qlookup=blackberries", "88.15 g", "43 kcal", "1.39 g", "0.49 g", "9.61 g", "5.3 g", "4.88 g", "29 mg", "0.62 mg", "20 mg", "22 mg", "162 mg", "1 mg", "0.53 mg", "21.0 mg", "0.020 mg", "0.026 mg", "0.646 mg", "0.030 mg", "25 µg", "11 µg", "1.17 mg", "19.8 µg"),
                new FoodItem("MALINY", "maliny", "malina1", "1.07", "30.09", "", "", "", "https://ndb.nal.usda.gov/ndb/foods/show/2374?fgcd=&manu=&lfacet=&format=&count=&max=35&offset=&sort=&qlookup=raspberries", "85.75 g", "52 kcal", "1.20 g", "0.65 g", "11.94 g", "6.5 g", "4.42 g", "25 mg", "0.69 mg", "22 mg", "29 mg", "151 mg", "1 mg", "0.42 mg", "26.2 mg", "0.032 mg", "0.038 mg", "0.598 mg", "0.055 mg", "21 µg", "2 µg", "0.87 mg", "7.8 µg"),
                new FoodItem("MIRABELKI", "mirabelki", "mirabelka1", "1.08", "30.09", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""),
                new FoodItem("MORELE", "morele", "morela", "1.07", "31.08", "", "", "", "https://ndb.nal.usda.gov/ndb/foods/show/2140?fgcd=&manu=&lfacet=&format=&count=&max=35&offset=&sort=&qlookup=apricots", "86.35 g", "48 kcal", "1.40 g", "0.39 g", "11.12 g", "2.0 g", "9.24 g", "13 mg", "0.39 mg", "10 mg", "23 mg", "259 mg", "1 mg", "0.20 mg", "10.0 mg", "0.030 mg", "0.040 mg", "0.600 mg", "0.054 mg", "9 µg", "96 µg", "0.89 mg", "3.3 µg"),
                new FoodItem("MORWA BIAŁA I CZARNA", "morwę białą i czarną", "morwa", "1.07", "31.08", "", "", "", "https://ndb.nal.usda.gov/ndb/foods/show/2278?fgcd=&manu=&lfacet=&format=&count=&max=35&offset=&sort=&qlookup=mulberries", "87.68 g", "43 kcal", "1.44 g", "0.39 g", "9.80 g", "1.7 g", "8.10 g", "39 mg", "1.85 mg", "18 mg", "38 mg", "194 mg", "10 mg", "0.12 mg", "36.4 mg", "0.029 mg", "0.101 mg", "0.620 mg", "0.050 mg", "6 µg", "1 µg", "0.87 mg", "7.8 µg"),
                new FoodItem("NEKTARYNKI", "nektarynki", "nektarynka1", "1.07", "30.09", "", "", "", "https://ndb.nal.usda.gov/ndb/foods/show/2279?fgcd=&manu=&lfacet=&format=&count=&max=35&offset=&sort=&qlookup=nectarines", "87.59 g", "44 kcal", "1.06 g", "0.32 g", "10.55 g", "1.7 g", "7.89 g", "6 mg", "0.28 mg", "9 mg", "26 mg", "201 mg", "", "0.17 mg", "5.4 mg", "0.034 mg", "0.027 mg", "1.125 mg", "0.025 mg", "5 µg", "17 µg", "0.77 mg", "2.2 µg"),
                new FoodItem("PIGWA", "pigwę", "pigwa1", "1.09", "30.11", "", "", "Pigwa zawiera witaminę C, garbniki, pektyny i olejek lotny.", "https://ndb.nal.usda.gov/ndb/foods/show/2369?fgcd=&manu=&lfacet=&format=&count=&max=35&offset=&sort=&qlookup=quince", "83.80 g", "57 kcal", "0.40 g", "0.10 g", "15.30 g", "1.9 g", "", "11 mg", "0.70 mg", "8 mg", "17 mg", "197 mg", "4 mg", "0.04 mg", "15.0 mg", "0.020 mg", "0.030 mg", "0.200 mg", "0.040 mg", "3 µg", "2 µg", "", ""),
                new FoodItem("PORZECZKA CZARNA", "czarną porzeczkę", "czarna_porzeczka", "15.06", "31.08", "", "", "Czarna porzeczka jest skarbnicą witaminy C, już 50 g owoców pokrywa dzienne zapotrzebowanie na nią u osoby dorosłej. Owoce czarnej porzeczki bogate są także w żelazo, pektyny (węglowodany o żelujących właściwościach, wspomagające odchudzanie) oraz liczne antyoksydanty.", "https://ndb.nal.usda.gov/ndb/foods/show/2195?fgcd=&manu=&lfacet=&format=&count=&max=35&offset=&sort=&qlookup=currants", "81.96 g", "63 kcal", "1.40 g", "0.41 g", "15.38 g", "", "", "55 mg", "1.54 mg", "24 mg", "59 mg", "322 mg", "2 mg", "0.27 mg", "181.0 mg", "0.050 mg", "0.050 mg", "0.300 mg", "0.066 mg", "", "12 µg", "1.00 mg", ""),
                new FoodItem("PORZECZKA BIAŁA I CZERWONA", "białą i czerwoną porzeczkę", "porzeczka1", "15.06", "31.08", "", "", "Porzeczka dostarcza nam duże ilości błonnika, aż 4,3 g /100g owocu. Jest bogata w witaminę C, witaminę K i żelazo. W 100g owoców znajduje się 1mg żelaza, to 1/10 zapotrzebowania dorosłego mężczyzny i 1/18 zapotrzebowania dorosłej kobiety. Dzięki dużej zawartości polifenoli, porzeczki zmniejszają bóle miesiączkowe i towarzyszące im skurcze.", "https://ndb.nal.usda.gov/ndb/foods/show/2196?fgcd=&manu=&lfacet=&format=&count=&max=35&offset=&sort=&qlookup=currants", "83.95 g", "56 kcal", "1.40 g", "0.20 g", "13.80 g", "4.3 g", "7.37 g", "33 mg", "1.00 mg", "13 mg", "44 mg", "275 mg", "1 mg", "0.23 mg", "41.0 mg", "0.040 mg", "0.050 mg", "0.100 mg", "0.070 mg", "8 µg", "2 µg", "0.10 mg", "11.0 µg"),
                new FoodItem("POZIOMKI", "poziomki", "poziomka1", "1.07", "31.08", "", "", "Poziomki podnoszą odporność organizmu, już około 130g tych owoców wystarczy, aby pokryć dzienne zapotrzebowanie dorosłego człowieka na witaminę C. Zawierają antyoksydanty (karotenoidy), które zwalczają wolne rodniki i garbniki (katechiny, ellagotaniny), które mają właściwości ściągające, działają przeciwbiegunkowo, przeciwzapalnie i przeciwkrwotocznie. ", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""),
                new FoodItem("ROKITNIK", "rokitnika", "rokitnik", "1.09", "31.10", "", "", "Rokitnik zawiera mnóstwo substancji, które mają pozytywny wpływ na zdrowie jak: antyoksydanty, flawonoidy, aminokwasy, a także nienasycone kwasy tłuszczowe, mikroelementy (m. in. potas, żelazo, fosfor, mangan, bor, wapń i krzem) oraz witaminy (A, B, C, D, E, K, P) i prowitaminy. Owoce rokitnika zaraz po dzikiej róży zawierają najwięcej witaminy C, około 200mg/100g. ", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""),
                new FoodItem("DZIKA RÓŻA", "dziką różę", "dzika_roza", "15.08", "31.10", "", "", "Owoce dzikiej róży zawierają rekordowe ilości witaminy C - mają jej około 15 razy więcej niż cytrusy. Jest w nich też wiele innych witamin - A, B1, B2, E, K, kwas foliowy, karotenoidy, flawonoidy, kwasy organiczne, garbniki, pektyny. Owoce dzikiej róży stosuje się w nadciśnieniu, chorobach serca, wątroby i jako lek witaminowy. ", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""),
                new FoodItem("ŚLIWKI", "śliwki", "sliwka1", "15.08", "15.10", "", "", "Śliwki znane są ze swoich właściwości przeczyszczających, za sprawą dużej zawartości pektyn (rodzaj błonnika). Zawierają także polifenole, w tym katechiny i kwas chlorogenowy, dzięki którym dieta obfitująca w śliwki ma działanie antynowotworowe oraz przeciwmiażdżycowe.", "https://ndb.nal.usda.gov/ndb/foods/show/2353?fgcd=&manu=&lfacet=&format=&count=&max=35&offset=&sort=&qlookup=plums", "87.23 g", "46 kcal", "0.70 g", "0.28 g", "11.42 g", "1.4 g", "9.92 g", "6 mg", "0.17 mg", "7 mg", "16 mg", "157 mg", "", "0.10 mg", "9.5 mg", "0.028 mg", "0.026 mg", "0.417 mg", "0.029 mg", "5 µg", "17 µg", "0.26 mg", "6.4 µg"),
                new FoodItem("TARNINA ŚLIWA", "tarninę śliwę", "tarnina_sliwa", "1.09", "30.11", "", "", "Tarnina śliwa swoją ciemnogranatową barwę zawdzięcza flawonoidom i antocyjanom, przeciwutleniaczom które chronią nas przed wolnymi rodnikami. Za sprawą zawartych w owocach tarniny garbników, mają one działanie przeciwbiegunkowe. Dodatkowo spowalniają ruchy robaczkowe jelit oraz wykazują działanie przeciwzapalne i antybakteryjne.", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""),
                new FoodItem("TRUSKAWKI", "truskawki", "truskawka1", "1.06", "30.06", "", "", "Truskawki podnoszą odporność organizmu, już około 130g tych owoców wystarczy, aby pokryć dzienne zapotrzebowanie dorosłego człowieka na witaminę C. Zawarte w nich pektyny pobudzają pracę jelit, a kwasy organiczne regulują trawienie i przyśpieszają przemianę materii. Dodatkowo truskawki mają tylko 32 kcal/100g.", "https://ndb.nal.usda.gov/ndb/foods/show/2385?fgcd=&manu=&lfacet=&format=&count=&max=35&offset=&sort=&qlookup=strawberry", "90.95 g", "32 kcal", "0.67 g", "0.30 g", "7.68 g", "2.0 g", "4.89 g", "16 mg", "0.41 mg", "13 mg", "24 mg", "153 mg", "1 mg", "0.14 mg", "58.8 mg", "0.024 mg", "0.022 mg", "0.386 mg", "0.047 mg", "24 µg", "1 µg", "0.29 mg", "2.2 µg"),
                new FoodItem("WINOGRONA", "winogrona", "winogrona1", "15.08", "31.10", "", "", "Winogrona zawierają antyoksydanty, które chronią organizm m. in. przed nowotworami i procesami starzenia, gdyż neutralizują wolne rodniki. Dostarczają jodu, który jest niezbędny dla funkcjonowania tarczycy, oraz są zasadotwórcze, co pomaga w pozbyciu się nagromadzonych w organizmie kwasów.", "https://ndb.nal.usda.gov/ndb/foods/show/2241?fgcd=&manu=&lfacet=&format=&count=&max=35&offset=&sort=&qlookup=grapes+red", "80.54 g", "69 kcal", "0.72 g", "0.16 g", "18.10 g", "0.9 g", "15.48 g", "10 mg", "0.36 mg", "7 mg", "20 mg", "191 mg", "2 mg", "0.07 mg", "3.2 mg", "0.069 mg", "0.070 mg", "0.188 mg", "0.086 mg", "2 µg", "3 µg", "0.19 mg", "14.6 µg"),
                new FoodItem("WIŚNIE", "wiśnie", "wisnia1", "1.06", "31.07", "", "", "Wiśnie zawierają dużo antyoksydantów (400 mg/100 g), które chronią organizm m. in. przed nowotworami i procesem starzenia, gdyż neutralizują wolne rodniki. Są także źródłem błonnika i zawierają stosunkowo niewiele kalorii.", "https://ndb.nal.usda.gov/ndb/foods/show/2183?fgcd=&manu=&lfacet=&format=&count=&max=35&offset=&sort=&qlookup=cherries", "82.25 g", "63 kcal", "1.06 g", "0.20 g", "16.01 g", "2.1 g", "12.82 g", "13 mg", "0.36 mg", "11 mg", "21 mg", "222 mg", "", "0.07 mg", "7.0 mg", "0.027 mg", "0.033 mg", "0.154 mg", "0.049 mg", "4 µg", "3 µg", "0.07 mg", "2.1 µg"),
                new FoodItem("ŻURAWINA", "żurawinę", "zurawina1", "1.09", "15.11", "", "", "Żurawina znana jest z dobrego wpływu na drogi moczowe, ma działanie przeciwbakteryjne i przeciwgrzybicze. Zawarte w niej przeciwutleniacze (procyjanidyny i fruktoza) uniemożliwiają przyczepianie się bakterii (w tym m.in. E.coli) do ścian nabłonka dróg moczowych i pęcherza, hamując tym samym ich rozwój. Żurawina jest także źródłem witaminy E i błonnika.", "https://ndb.nal.usda.gov/ndb/foods/show/2191?fgcd=&manu=&lfacet=&format=&count=&max=35&offset=&sort=&qlookup=Cranberries", "87.32 g", "46 kcal", "0.46 g", "0.13 g", "11.97 g", "3.6 g", "4.27 g", "8 mg", "0.23 mg", "6 mg", "11 mg", "80 mg", "2 mg", "0.09 mg", "14.0 mg", "0.012 mg", "0.020 mg", "0.101 mg", "0.057 mg", "1 µg", "3 µg", "1.32 mg", "5.0 µg"),
                new FoodItem("GRUSZKI", "gruszki", "gruszka2", "-", "-", "", "", "Gruszki podobnie jak jabłka bogate są w pektyny, które wiążą metale ciężkie i toksyny, a następnie razem z nimi usuwane są z organizmu. Zawierają jod, który jest niezbędny do prawidłowego działania tarczycy i zapobiega jej schorzeniom. Dostarczają także bor, który podwyższa i stabilizuje hormony odpowiedzialne za przemianę materii, a także pozytywnie wpływa na procesy przyswajania wapnia, magnezu, fosforu i przemian energetycznych.", "https://ndb.nal.usda.gov/ndb/foods/show/2326?fgcd=&manu=&lfacet=&format=&count=&max=35&offset=&sort=&qlookup=pears", "83.96 g", "57 kcal", "0.36 g", "0.14 g", "15.23 g", "3.1 g", "9.75 g", "9 mg", "0.18 mg", "7 mg", "12 mg", "116 mg", "1 mg", "0.10 mg", "4.3 mg", "0.012 mg", "0.026 mg", "0.161 mg", "0.029 mg", "7 µg", "1 µg", "0.12 mg", "4.4 µg"),
                new FoodItem("JABŁKA", "jabłka", "jablko3", "-", "-", "", "", "Jabłka, w szczególności ich skórka, bogate są w pektyny, które wiążą metale ciężkie i toksyny, a następnie razem z nimi usuwane są z organizmu. Ponadto regulują gospodarkę kwasów żółciowych i obniżają poziom cholesterolu. Jabłka dają uczucie sytości, mając przy tym proporcjonalnie mało kalorii. ", "https://ndb.nal.usda.gov/ndb/foods/show/2122?fgcd=&manu=&lfacet=&format=&count=&max=35&offset=&sort=&qlookup=apples", "85.56 g", "52 kcal", "0.26 g", "0.17 g", "13.81 g", "2.4 g", "10.39 g", "6 mg", "0.12 mg", "5 mg", "11 mg", "107 mg", "1 mg", "0.04 mg", "4.6 mg", "0.017 mg", "0.026 mg", "0.091 mg", "0.041 mg", "3 µg", "3 µg", "0.18 mg", "2.2 µg"),
                new FoodItem("ORZECHY LASKOWE", "orzechy laskowe", "orzech_laskowy1", "-", "-", "", "", "Orzechy laskowe zawierają dużo błonnika, żelaza, magnezu, witaminy E oraz kwasu foliowego. Już 50 g orzechów pokrywa niemal całe dzienne zapotrzebowanie na witaminę E, która nazywana jest „witaminą młodości”, ponieważ opóźnia procesy starzenia. Trzeba jednak jeść je z umiarem, ponieważ dostarczają dużo kalorii.", "https://ndb.nal.usda.gov/ndb/foods/show/3666?fgcd=&manu=&lfacet=&format=&count=&max=35&offset=&sort=&qlookup=hazelnuts", "5.31 g", "628 kcal", "14.95 g", "60.75 g", "16.70 g", "9.7 g", "4.34 g", "114 mg", "4.70 mg", "163 mg", "290 mg", "680 mg", "", "2.45 mg", "6.3 mg", "0.643 mg", "0.113 mg", "1.800 mg", "0.563 mg", "113 µg", "1 µg", "15.03 mg", "14.2 µg"),
                new FoodItem("ORZECHY WŁOSKIE", "orzechy włoskie", "orzech_wloski1", "-", "-", "", "", "Po orzechy włoskie powinny sięgać szczególnie osoby, które nie jedzą ryb, ponieważ orzechy tak samo jak ryby, zawierają potrzebne dla zdrowia i rozwoju wielonienasycone kwasy tłuszczowe. Bogate są także w żelazo, magnez, fosfor, potas, kwas foliowy oraz błonnik.", "https://ndb.nal.usda.gov/ndb/foods/show/3690?fgcd=&manu=&lfacet=&format=&count=&max=35&offset=&sort=&qlookup=walnuts", "4.07 g", "654 kcal", "15.23 g", "65.21 g", "13.71 g", "6.7 g", "2.61 g", "98 mg", "2.91 mg", "158 mg", "346 mg", "441 mg", "2 mg", "3.09 mg", "1.3 mg", "0.341 mg", "0.150 mg", "1.125 mg", "0.537 mg", "98 µg", "1 µg", "0.70 mg", "2.7 µg")
        };
        getFoodItemDao().insertAll(items);
    }
}
