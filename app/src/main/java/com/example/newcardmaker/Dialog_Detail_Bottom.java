package com.example.newcardmaker;

import static android.util.Log.ASSERT;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.example.newcardmaker.invite_online_database.Adapter_Category_Wallpaper;
import com.example.newcardmaker.invite_online_database.invite_AppConstants;
import com.example.newcardmaker.invite_online_database.invite_ItemSubCat;
import com.example.newcardmaker.invite_online_database.invite_JSONParser;
import com.example.newcardmaker.invite_online_database.invite_Methods;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.RequestBody;

public class Dialog_Detail_Bottom extends BottomSheetDialogFragment {

    // ── Listener — MainActivity callback
    public interface OnWallpaperSelectedListener {
        void onWallpaperSelected(String imageUrl);
    }
    private OnWallpaperSelectedListener wallpaperSelectedListener;

    public void setOnWallpaperSelectedListener(
            OnWallpaperSelectedListener l) {
        this.wallpaperSelectedListener = l;
    }

    // ── Args
    private static final String ARG_IMAGES = "arg_images";
    private ArrayList<String> passedImages;

    // ── Views
    private RecyclerView rvCategory, rvImages;
    private ProgressBar  progressBar;
    private ImageView    btnBack, btnClose, arry_list;

    // ── Pagination
    private int     page       = 1;
    private boolean isLoading  = false;
    private boolean isLastPage = false;

    // ── Utils
    private invite_Methods methods;

    // ── Thread + Handler
    private final ExecutorService executor =
            Executors.newSingleThreadExecutor();
    private final Handler mainHandler =
            new Handler(Looper.getMainLooper());

    // ── Data
    private final ArrayList<invite_ItemSubCat> categoryList =
            new ArrayList<>();
    private final ArrayList<Item_OneImages> imageList =
            new ArrayList<>();
    private final ArrayList<Item_OneImages> imageTemp =
            new ArrayList<>();

    // ── Cache
    private final HashMap<String, ArrayList<Item_OneImages>>
            imageCache = new HashMap<>();
    private final HashMap<String, Integer>
            totalCache = new HashMap<>();

    // ── Prefs
    private SharedPreferences prefs;
    private final Gson gson = new Gson();
    private static final String PREF_NAME         = "photo_wall_pref";
    private static final String KEY_LAST_CAT      = "last_cat_id";
    private static final String KEY_IMAGES_PREFIX = "images_";
    private static final String KEY_TOTAL_PREFIX  = "total_";

    // ── Adapters
    private Adapter_Category_Wallpaper categoryAdapter;
    private Adapter_Image_Wallpaper    imageAdapter;

    // ── State
    private String              currentCatId = "";
    private LinearLayoutManager imagesLayoutManager;
    private static final String CUSTOM_CAT_ID = "custom_0";
    private SnapHelper          categorySnapHelper;

    // ── Factory — same as original (no args needed)
    public static Dialog_Detail_Bottom newInstanceFromArrayList() {
        Dialog_Detail_Bottom f = new Dialog_Detail_Bottom();
        f.setArguments(new Bundle());
        return f;
    }

    // ════════════════════════════════════════
    // onCreateView
    // ════════════════════════════════════════
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(
                R.layout.dialog_detail_bottom,
                container, false);

        rvCategory  = view.findViewById(R.id.rv_category);
        rvImages    = view.findViewById(R.id.rv_images);
        progressBar = view.findViewById(R.id.pb_detail);
        btnBack     = view.findViewById(R.id.btn_back);
        btnClose    = view.findViewById(R.id.btn_close);
        arry_list   = view.findViewById(R.id.arry_list);

        prefs   = requireContext().getSharedPreferences(
                PREF_NAME, Context.MODE_PRIVATE);
        methods = new invite_Methods(requireActivity());

        if (getArguments() != null)
            passedImages = getArguments()
                    .getStringArrayList(ARG_IMAGES);

        setupCategoryRV();
        setupImagesRV();
        setupTopButtons();
        setupCategoryClick();

        rvCategory.setVisibility(View.VISIBLE);
        rvImages.setVisibility(View.GONE);
        if (progressBar != null)
            progressBar.setVisibility(View.GONE);

        loadCategories();
        return view;
    }

    // ════════════════════════════════════════
    // TOP BUTTONS — same as original
    // ════════════════════════════════════════
    private void setupTopButtons() {
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> {
                if (rvImages != null &&
                        rvImages.getVisibility() == View.VISIBLE) {
                    rvImages.setVisibility(View.GONE);
                    if (progressBar != null)
                        progressBar.setVisibility(View.GONE);
                    isLoading  = false;
                    isLastPage = false;
                    page       = 1;
                } else {
                    dismiss();
                }
            });
        }
        if (btnClose != null)
            btnClose.setOnClickListener(v -> dismiss());

        if (arry_list != null) {
            arry_list.setOnClickListener(v -> {
                if (categoryAdapter != null) {
                    categoryAdapter.setSelectedPosition(0);
                    rvCategory.smoothScrollToPosition(0);
                }
                showPassedImages();
            });
        }
    }

    // ════════════════════════════════════════
    // CATEGORY RV — same as original
    // ════════════════════════════════════════
    private void setupCategoryRV() {
        LinearLayoutManager manager = new LinearLayoutManager(
                getContext(), RecyclerView.HORIZONTAL, false);
        rvCategory.setLayoutManager(manager);
        rvCategory.setHasFixedSize(true);
        categorySnapHelper = new LinearSnapHelper();
        categorySnapHelper.attachToRecyclerView(rvCategory);
    }
        // ════════════════════════════════════════
    // LOAD CATEGORIES — Thread replaces AsyncTask
    // ════════════════════════════════════════
    private void loadCategories() {
        if (progressBar != null)
            progressBar.setVisibility(View.VISIBLE);
        categoryList.clear();

        // ✅ Exact same getAPIRequest as original
        RequestBody body = methods.getAPIRequest(
                invite_AppConstants.METHOD_CAT_PHOTOWALL,
                0,
                "", "", "", "4",
                "", "", "", "", "", "", "",
                "", "", "", "", null);

        executor.execute(() -> {
            try {
                String json = invite_JSONParser.okhttpPost(
                        invite_AppConstants.SERVER_URL, body);

                Log.e("DDB++", "CAT len=" +
                        (json != null ? json.length() : 0));

                // Parse: {"QUOTES_DIARY":{"image_quotes_cat":[...]}}
                final ArrayList<invite_ItemSubCat> apiList =
                        parseCategoryJson(json);

                mainHandler.post(() -> {
                    if (!isAdded()) return;
                    if (progressBar != null)
                        progressBar.setVisibility(View.GONE);

                    categoryList.clear();

                    if (apiList != null)
                        categoryList.addAll(apiList);

                    categoryAdapter =
                            new Adapter_Category_Wallpaper(
                                    getActivity(), categoryList);
                    rvCategory.setAdapter(categoryAdapter);
                    categoryAdapter.setSelectedPosition(0);
                    rvCategory.scrollToPosition(0);

                    // Auto-load last cat
                    String lastCat = prefs.getString(
                            KEY_LAST_CAT, "");
                    if (lastCat != null &&
                            !lastCat.trim().isEmpty() &&
                            !CUSTOM_CAT_ID.equals(lastCat)) {
                        int pos = findCategoryPositionById(
                                lastCat);
                        if (pos >= 0) {
                            categoryAdapter
                                    .setSelectedPosition(pos);
                            rvCategory.post(() ->
                                    rvCategory
                                            .smoothScrollToPosition(pos));
                        }
                        showImages(lastCat);
                    }
                });

            } catch (Exception e) {
                Log.e("DDB", "loadCat error: " + e);
                mainHandler.post(() -> {
                    if (!isAdded()) return;
                    if (progressBar != null)
                        progressBar.setVisibility(View.GONE);
                });
            }
        });
    }

    // ── Parse category JSON
    // {"QUOTES_DIARY":{"image_quotes_cat":[{cid, category_name}]}}
    private ArrayList<invite_ItemSubCat> parseCategoryJson(
            String json) {
        ArrayList<invite_ItemSubCat> list = new ArrayList<>();
        if (json == null || json.trim().isEmpty()) return list;
        try {
            JSONObject root = new JSONObject(json);

            // Get QUOTES_DIARY inner object
            JSONObject inner = null;
            if (root.has("QUOTES_DIARY")) {
                Object qd = root.get("QUOTES_DIARY");
                if (qd instanceof JSONObject)
                    inner = (JSONObject) qd;
            }
            if (inner == null) {
                Iterator<String> keys = root.keys();
                while (keys.hasNext()) {
                    Object v = root.get(keys.next());
                    if (v instanceof JSONObject) {
                        inner = (JSONObject) v; break;
                    }
                }
            }
            if (inner == null) return list;

            addFromArray(inner, "image_quotes_cat", list);
            addFromArray(inner, "text_quotes_cat",  list);

        } catch (Exception e) {
            Log.e("DDB", "parseCat error: " + e);
        }
        return list;
    }

    private void addFromArray(JSONObject parent, String key,
                              ArrayList<invite_ItemSubCat> out) {
        try {
            if (!parent.has(key)) return;
            JSONArray arr = parent.getJSONArray(key);
            for (int i = 0; i < arr.length(); i++) {
                JSONObject o = arr.getJSONObject(i);
                String cid  = o.optString("cid", "");
                String name = o.optString("category_name","");
                String img  = o.optString("category_image","");
                if (!cid.isEmpty() && !name.isEmpty()) {
                    // ✅ invite_ItemSubCat(id, catid, name, img)
                    out.add(new invite_ItemSubCat(
                            cid, cid, name, img));
                    Log.e("DDB", "Cat: " + cid + " " + name);
                }
            }
        } catch (Exception ignore) {}
    }

    private int findCategoryPositionById(String catId) {
        if (catId == null) return -1;
        for (int i = 0; i < categoryList.size(); i++) {
            try {
                // ✅ getId() — exact method from invite_ItemSubCat
                if (catId.equals(categoryList.get(i).getId()))
                    return i;
            } catch (Exception ignore) {}
        }
        return -1;
    }

    // ════════════════════════════════════════
    // CATEGORY CLICK — same as original
    // ════════════════════════════════════════
    private void setupCategoryClick() {
        rvCategory.addOnItemTouchListener(
                new invite_AppConstants.RecyclerTouchListener(
                        requireContext(), rvCategory,
                        new invite_AppConstants
                                .RecyclerTouchListener.ClickListener() {

                            @Override
                            public void onClick(View view, int position) {
                                if (position < 0 ||
                                        position >= categoryList.size())
                                    return;
                                if (categoryAdapter == null) return;

                                categoryAdapter.setSelectedPosition(
                                        position);

                                if (position == 0) {
                                    showPassedImages();
                                    return;
                                }

                                String catId = "";
                                try {
                                    // ✅ getId() exact
                                    catId = categoryList
                                            .get(position).getId();
                                } catch (Exception ignore) {}

                                if (catId == null ||
                                        catId.trim().isEmpty()) return;

                                showImages(catId);
                            }

                            @Override
                            public void onLongClick(View view,
                                                    int position) {}
                        }));
    }

    // ════════════════════════════════════════
    // IMAGES RV — same as original
    // ════════════════════════════════════════
    private void setupImagesRV() {
        imagesLayoutManager = new LinearLayoutManager(
                getContext(), RecyclerView.HORIZONTAL, false);
        rvImages.setLayoutManager(imagesLayoutManager);
        rvImages.setHasFixedSize(true);

        rvImages.addOnScrollListener(
                new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrolled(@NonNull RecyclerView rv,
                                           int dx, int dy) {
                        super.onScrolled(rv, dx, dy);
                        Log.println(ASSERT, "scall",
                                "scall " + dx + " / " + dy);

                        if (dx <= 0) return;
                        if (isLoading || isLastPage) return;

                        int visible = imagesLayoutManager.getChildCount();
                        int total   = imagesLayoutManager.getItemCount();
                        int first   = imagesLayoutManager
                                .findFirstVisibleItemPosition();

                        if (first >= 0 &&
                                (visible + first) >= (total - 2)) {
                            isLoading = true;
                            page++;
                            loadImagesNextPage(currentCatId);
                        }
                    }
                });
    }

    // ════════════════════════════════════════
    // SHOW PASSED IMAGES — same as original
    // ════════════════════════════════════════
    private void showPassedImages() {
        if (passedImages == null || passedImages.isEmpty()) {
            Log.d("DDB", "passedImages null/empty");
            return;
        }

        isLoading    = false;
        isLastPage   = true;
        page         = 1;
        currentCatId = "";

        rvCategory.setVisibility(View.VISIBLE);
        rvImages.setVisibility(View.VISIBLE);
        if (progressBar != null)
            progressBar.setVisibility(View.GONE);

        imageList.clear();
        imageTemp.clear();

        for (String url : passedImages) {
            Item_OneImages item = new Item_OneImages();
            item.setImage(url);
            item.setImageBig(url);
            imageList.add(item);
            imageTemp.add(item);
        }

        imageAdapter = null;
        rvImages.setAdapter(null);
        setImageAdapterIfNeeded();
    }

    // ════════════════════════════════════════
    // SHOW IMAGES — same as original
    // ════════════════════════════════════════
    private void showImages(String catId) {
        currentCatId = catId;
        page         = 1;
        isLoading    = false;
        isLastPage   = false;

        rvCategory.setVisibility(View.VISIBLE);
        rvImages.setVisibility(View.VISIBLE);

        // Memory cache
        ArrayList<Item_OneImages> cached =
                imageCache.get(catId);
        if (cached != null && !cached.isEmpty()) {
            imageList.clear(); imageTemp.clear();
            imageList.addAll(cached);
            imageTemp.addAll(cached);
            imageAdapter = null;
            rvImages.setAdapter(null);
            setImageAdapterIfNeeded();
            if (progressBar != null)
                progressBar.setVisibility(View.GONE);
            Integer total = totalCache.get(catId);
            if (total != null && imageList.size() >= total)
                isLastPage = true;
            return;
        }

        // Prefs cache
        ArrayList<Item_OneImages> prefList =
                loadImagesFromPrefs(catId);
        if (prefList != null && !prefList.isEmpty()) {
            imageList.clear(); imageTemp.clear();
            imageList.addAll(prefList);
            imageTemp.addAll(prefList);
            imageAdapter = null;
            rvImages.setAdapter(null);
            setImageAdapterIfNeeded();
            if (progressBar != null)
                progressBar.setVisibility(View.GONE);
            int total = loadTotalFromPrefs(catId);
            if (total > 0) {
                totalCache.put(catId, total);
                if (imageList.size() >= total)
                    isLastPage = true;
            }
            imageCache.put(catId, new ArrayList<>(imageList));
            return;
        }

        // API
        imageList.clear(); imageTemp.clear();
        imageAdapter = null;
        rvImages.setAdapter(null);
        loadImagesFirstPage(catId);
    }

    // ════════════════════════════════════════
    // LOAD IMAGES FIRST PAGE — Thread
    // ════════════════════════════════════════
    private void loadImagesFirstPage(String catId) {
        if (progressBar != null)
            progressBar.setVisibility(View.VISIBLE);


        Log.println(ASSERT,"cat+id ",catId+"");
        // ✅ Exact same getAPIRequest as original
        RequestBody requestBody = methods.getAPIRequest(
                invite_AppConstants.METHOD_IMAGE_PHOTOWALL,
                1,
                "", "", "",
                catId,
                "", "", "", "", "", "", "",
                "", "", invite_AppConstants.itemUser.getId(),
                "", null);

        executor.execute(() -> {
            try {
                String json = invite_JSONParser.okhttpPost(
                        invite_AppConstants.SERVER_URL,
                        requestBody);
                Log.e("DDB11", "IMG: "+json);
                Log.e("DDB", "IMG: " + (json != null ?
                        json.substring(0,
                                Math.min(200, json.length()))
                        : "NULL"));

                // Parse image response
                final ArrayList<Item_OneImages> list =
                        parseImageJson(json);
                final int total = parseTotalFromJson(json);

                mainHandler.post(() -> {
                    if (!isAdded()) return;
                    if (progressBar != null)
                        progressBar.setVisibility(View.GONE);
                    isLoading = false;

                    Log.e("DDB", "Images: " + list.size()
                            + " total=" + total);

                    if (list.isEmpty()) return;

                    imageList.clear(); imageTemp.clear();
                    imageList.addAll(list);
                    imageTemp.addAll(list);

                    imageCache.put(catId,
                            new ArrayList<>(imageList));
                    totalCache.put(catId, total);
                    saveImagesToPrefs(catId, imageList, total);

                    setImageAdapterIfNeeded();

                    if (imageList.size() >= total && total > 0)
                        isLastPage = true;
                });

            } catch (Exception e) {
                Log.e("DDB", "loadImages error: " + e);
                mainHandler.post(() -> {
                    if (!isAdded()) return;
                    if (progressBar != null)
                        progressBar.setVisibility(View.GONE);
                    isLoading = false;
                });
            }
        });
    }

    // ════════════════════════════════════════
    // LOAD IMAGES NEXT PAGE
    // ════════════════════════════════════════
    private void loadImagesNextPage(String catId) {
        if (catId == null || catId.trim().isEmpty()) {
            isLoading = false; return;
        }

        Integer total = totalCache.get(catId);
        if (total != null && imageList.size() >= total) {
            isLastPage = true; isLoading = false; return;
        }

        RequestBody requestBody = methods.getAPIRequest(
                invite_AppConstants.METHOD_IMAGE_PHOTOWALL,
                page,
                "", "", "",
                catId,
                "", "", "", "", "", "", "",
                "", "", invite_AppConstants.itemUser.getId(),
                "", null);

        executor.execute(() -> {
            try {
                String json = invite_JSONParser.okhttpPost(
                        invite_AppConstants.SERVER_URL,
                        requestBody);

                final ArrayList<Item_OneImages> list =
                        parseImageJson(json);
                final int newTotal = parseTotalFromJson(json);

                mainHandler.post(() -> {
                    if (!isAdded()) return;
                    isLoading = false;

                    if (list == null || list.isEmpty()) {
                        isLastPage = true; return;
                    }

                    imageList.addAll(list);
                    imageTemp.addAll(list);

                    // ✅ same as original
                    if (imageAdapter != null)
                        imageAdapter.notifyDataSetChanged();

                    imageCache.put(catId,
                            new ArrayList<>(imageList));
                    totalCache.put(catId, newTotal);
                    saveImagesToPrefs(catId, imageList,
                            newTotal);

                    if (imageList.size() >= newTotal
                            && newTotal > 0)
                        isLastPage = true;
                });

            } catch (Exception e) {
                Log.e("DDB", "nextPage error: " + e);
                mainHandler.post(() -> isLoading = false);
            }
        });
    }

    // ════════════════════════════════════════
    // PARSE IMAGE JSON
    // {"QUOTES_DIARY": [{id, quote_image_b, num}]}
    // ════════════════════════════════════════
    private ArrayList<Item_OneImages> parseImageJson(
            String json) {
        ArrayList<Item_OneImages> list = new ArrayList<>();
        if (json == null || json.trim().isEmpty()) return list;
        try {
            JSONArray arr = extractArray(json);
            if (arr == null) return list;

            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj = arr.getJSONObject(i);
                if (obj.has("success") ||
                        obj.has("verify_status")) continue;

                // ✅ "quote_image_b" = actual image key
                String imgBig = firstHttp(obj,
                        "quote_image_b",
                        "image_big", "image",
                        "image_url", "photo",
                        "img", "url");

                if (!imgBig.isEmpty()) {
                    Item_OneImages item = new Item_OneImages();
                    item.setImage(imgBig);
                    item.setImageBig(imgBig);
                    list.add(item);
                }
            }
        } catch (Exception e) {
            Log.e("DDB", "parseImageJson: " + e);
        }
        Log.e("DDB", "Parsed: " + list.size());
        return list;
    }

    // ── Extract JSONArray from response
    private JSONArray extractArray(String json) {
        if (json == null) return null;
        try {
            String t = json.trim();
            if (t.startsWith("["))
                return new JSONArray(json);
            if (t.startsWith("{")) {
                JSONObject root = new JSONObject(json);
                // ✅ QUOTES_DIARY = array in image response
                if (root.has("QUOTES_DIARY")) {
                    Object qd = root.get("QUOTES_DIARY");
                    if (qd instanceof JSONArray)
                        return (JSONArray) qd;
                }
                // Fallback
                Iterator<String> rk = root.keys();
                while (rk.hasNext()) {
                    Object v = root.get(rk.next());
                    if (v instanceof JSONArray)
                        return (JSONArray) v;
                }
            }
        } catch (Exception ignore) {}
        return null;
    }

    // ── Parse total — "num" field
    private int parseTotalFromJson(String json) {
        try {
            JSONArray arr = extractArray(json);
            if (arr != null && arr.length() > 0) {
                JSONObject first = arr.getJSONObject(0);
                if (first.has("num"))
                    return first.optInt("num", 0);
                if (first.has("total"))
                    return first.optInt("total", 0);
            }
        } catch (Exception ignore) {}
        return 0;
    }

    private String firstHttp(JSONObject obj, String... keys) {
        for (String k : keys) {
            String v = obj.optString(k, "").trim();
            if (!v.isEmpty() && v.startsWith("http"))
                return v;
        }
        return "";
    }

    // ════════════════════════════════════════
    // SET IMAGE ADAPTER — same as original + click
    // ════════════════════════════════════════
    private void setImageAdapterIfNeeded() {
        if (imageAdapter == null) {
            // ✅ Adapter_Image_Wallpaper with OnImageClickListener
            imageAdapter = new Adapter_Image_Wallpaper(
                    getActivity(),
                    false,
                    imageList,
                    imageTemp,
                    // ✅ Click → wallpaperSelectedListener
                    imageUrl -> {
                        if (wallpaperSelectedListener != null)
                            wallpaperSelectedListener
                                    .onWallpaperSelected(imageUrl);
                        dismiss();
                    });
            rvImages.setAdapter(imageAdapter);
            Log.e("DDB", "Adapter set: "
                    + imageList.size());
        } else {
            imageAdapter.notifyDataSetChanged();
        }
    }

    // ════════════════════════════════════════
    // PREFS — same as original
    // ════════════════════════════════════════
    private void saveImagesToPrefs(String catId,
                                   ArrayList<Item_OneImages> list, int total) {
        if (catId == null) return;
        prefs.edit()
                .putString(KEY_LAST_CAT, catId)
                .putString(KEY_IMAGES_PREFIX + catId,
                        gson.toJson(list))
                .putInt(KEY_TOTAL_PREFIX + catId, total)
                .apply();
    }

    private ArrayList<Item_OneImages> loadImagesFromPrefs(
            String catId) {
        try {
            String json = prefs.getString(
                    KEY_IMAGES_PREFIX + catId, null);
            if (json == null) return null;
            Type type = new TypeToken<
                    ArrayList<Item_OneImages>>(){}.getType();
            return gson.fromJson(json, type);
        } catch (Exception e) { return null; }
    }

    private int loadTotalFromPrefs(String catId) {
        return prefs.getInt(KEY_TOTAL_PREFIX + catId, 0);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        executor.shutdown();
    }
}
