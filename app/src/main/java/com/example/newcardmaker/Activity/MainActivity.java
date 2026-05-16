package com.example.newcardmaker.Activity;

import static android.util.Log.ASSERT;
import static android.view.View.VISIBLE;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.newcardmaker.DeletedTextAdapter;
import com.example.newcardmaker.Dialog_Detail_Bottom;
import com.example.newcardmaker.FreehandCropActivity;
import com.example.newcardmaker.ImageCropActivity;
import com.example.newcardmaker.ImageFileManager;
import com.example.newcardmaker.LockedLayersAdapter;
import com.example.newcardmaker.PdfFileManager;
import com.example.newcardmaker.PdfViewerActivity;
import com.example.newcardmaker.R;
import com.example.newcardmaker.RotationGestureDetector;
import com.example.newcardmaker.StrokeTextView;
import com.example.newcardmaker.invite_online_database.invite_AppConstants;
import com.example.newcardmaker.invite_online_database.invite_EndlessRecyclerViewScrollListener1;
import com.example.newcardmaker.invite_online_database.invite_JSONParser;
import com.example.newcardmaker.invite_online_database.invite_Methods;
import com.example.newcardmaker.invite_photo_frame;
import com.example.newcardmaker.invite_sticker.invite_sticker_main_category;
import com.yalantis.ucrop.UCrop;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {


    private PopupWindow arcPopupWindow;
    // Class top માં
    private List<List<View>> allGroups = new ArrayList<>();
    private int selectedGroupIndex = -1;
    private static final int REQUEST_LOCATION_PICKER = 600;
    private PopupWindow multiSelectPopup = null;
    private List<JSONObject> deletedStickersList = new ArrayList<>();
    private TextView btnMsDelete;
    private TextView btnMsSizeMinus, btnMsSizePlus;
    private TextView btnMsUp, btnMsDown, btnMsLeft, btnMsRight;
    private TextView btnMsSelectAll, btnMsUnselect, btnMsCopy, btnMsCancel;
    private TextView btnSpeedSlow, btnSpeedFast;
    private String currentFilter = "ALL";

    private static final int REQUEST_FRAME_PHOTO_ADJUST = 404;

    private android.widget.FrameLayout gridLoaderOverlay = null;

    private static final int REQUEST_GRID_LIST = 500;  // grid list open request code

    // Grid data store — key = gridIndex, value = cells JSON string
    private final android.util.SparseArray<String> gridCellsMap = new android.util.SparseArray<>();

    // Grid bitmap cache — key = gridIndex, value = rendered bitmap
    private final android.util.SparseArray<android.graphics.Bitmap> gridBitmapCache
            = new android.util.SparseArray<>();


    private static final int REQUEST_TEXT_BG_IMAGE = 701;
    private StrokeTextView pendingTextBgTarget = null;
    private static final int REQUEST_GRID_CELL_PHOTO = 800;
    private ImageView pendingGridCellTarget = null;
    private int pendingGridCellIdx = -1;
    private List<JSONObject> pendingGridCellDataList = null;
    private String pendingGridShape = "ROUNDED";
    private int pendingGridCellSize = 200;

    private static final int REQUEST_GRID_MULTI_PHOTO = 801;
    private RelativeLayout currentGridListTarget = null;
    private RelativeLayout pendingGridMultiContainer = null;
    private int pendingGridMultiStartIdx = 0;
    private List<JSONObject> pendingGridMultiDataList = null;
    PopupWindow applyTouchListener;
    private boolean isGridSwapMode = false;
    private static final int REQUEST_GRID_LIST_ACTIVITY = 950;

    private ImageView currentFrameTargetSticker = null;

    private static final int REQUEST_PHOTO_SET_ACTIVITY = 402;

    private static final int REQUEST_FREEHAND_CROP = 501;
    private ImageView pendingFreehandTarget = null;

    private int pendingFrameOverlayColor = Color.TRANSPARENT; // default = no color
    // Class top માં
    private String pendingFrameUrl = "";
    private String pendingMaskUrl = "";      // ✅ mask_image
    private String pendingImageTopUrl = "";  // ✅ image_top

    private PopupWindow arcTextPopup = null;
    private int arcPopupLastX = 20;
    private int arcPopupLastY = 700;
    private boolean isArcPopupMoved = false;

    private PopupWindow frameImageControlsPopup = null;
    private int framePopupLastX = 30;
    private int framePopupLastY = 300;
    private boolean isFramePopupMoved = false;
    private int frameOrigW = 0, frameOrigH = 0;


    private static class MergeResult {
        Bitmap finalBitmap;
        Bitmap userMaskedBitmap;

        MergeResult(Bitmap finalBitmap, Bitmap userMaskedBitmap) {
            this.finalBitmap = finalBitmap;
            this.userMaskedBitmap = userMaskedBitmap;
        }
    }

    private static final int REQUEST_TEXT_BG_IMAGE1 = 700;


    // ── Selection controls last position
    private int selControlsLastX = 50;
    private int selControlsLastY = 400;
    private boolean isSelControlsMoved = false; // ✅ new


    private static final int REQUEST_IMAGE_CROP = 500;
    private ImageView pendingCropTarget = null;

    private Set<View> lockedViews = new HashSet<>();

    private static final int REQUEST_FRAME_GALLERY_IMAGE = 400;
    private static final int REQUEST_UCROP = 401;

    private AlertDialog framePickerDialog = null;

    private static final int REQUEST_STICKER_PICK = 300;

    private LinearLayout btnMultiSelect;
    public static ArrayList<String> all_data;
    private final OkHttpClient httpClient = new OkHttpClient();
    // આ existing fields ની નીચે ઉમેરો:
    private float dX, dY;  // already exist

    ArrayList<JSONObject> cellDataList = new ArrayList<>();

    // Fields — class top માં
    private android.widget.EditText voiceTargetInput = null;
    private static final int REQUEST_VOICE_INPUT = 200;

    private RelativeLayout mainLayout;
    private android.widget.ImageView main_image_view;
    private StrokeTextView currentlySelectedTextView = null;
    private String currentOpenFilePath = null;

    private List<JSONObject> allPagesData = new ArrayList<>();
    private List<JSONObject> deletedTextsList = new ArrayList<>();
    private List<JSONObject> deletedPagesList = new ArrayList<>();

    private String currentImageUrl = "";
    private int currentPageIndex = 0;
    private TextView txtPageIndicator;


    private static final int PICK_STICKER_IMAGE = 101;
    private View currentlySelectedView = null; // TextView અને ImageView બંને માટે
    private ScaleGestureDetector scaleGestureDetector;
    // Existing ScaleGestureDetector ની નીચે ઉમેરો:
    private RotationGestureDetector rotationGestureDetector;
    private float scaleFactor = 1.0f; // ડિફોલ્ટ સ્કેલ

    // Multi-select માટે
    private List<View> selectedViews = new ArrayList<>();
    private boolean isMultiSelectMode = false;
    // Original — select થાય ત્યારે એક જ વાર save, પછી ક્યારેય change નહીં
    private List<Float> selectedOriginalSizes = new ArrayList<>();

    // Group move માટે
    private float groupMoveStartX, groupMoveStartY;
    private List<float[]> groupStartPositions = new ArrayList<>();
    private boolean isGroupMoving = false;

    private PopupWindow currentStickerToolbarPopup = null;
    private PopupWindow selectionControlsPopup = null;
    private float selOriginalSize = 0f; // Text size px
    private int selOriginalW = 0, selOriginalH = 0; // Image size
    private static final int SEL_MOVE_STEP = 10;

    // Multi-select toolbar views
    private LinearLayout multiSelectToolbar;
    private android.widget.SeekBar seekMultiSize;
    private TextView tvSizeLabel;


    // Original sizes save (seek reset માટે)

    private android.widget.SeekBar seekMoveSpeed;
    private TextView tvMoveSpeed;


    // Speed steps — index = seekbar progress
    private final int[] SPEED_STEPS = {1, 5, 10, 15, 20, 30, 50, 80, 100, 150};
    private int currentMoveStep = 10; // default


    private static final int MOVE_STEP = 10; // દર click = 10px move

    private TextView tabAllLayers;
    private TextView tabLockedLayers;
    private TextView tabUnlockedLayers;

    String currentGridFrameUrl;
    String currentGridFrameMaskUrl;
    String currentGridFrameTopUrl;

    private androidx.drawerlayout.widget.DrawerLayout drawerLayout;
    private androidx.recyclerview.widget.RecyclerView rvLockedLayers;
    private TextView tvLockCount;
    private TextView tvLockEmpty;
    private LockedLayersAdapter lockedLayersAdapter;

    private PopupWindow gridEditPopup = null;
    private RelativeLayout currentlySelectedGrid = null;

    private ImageView selectedGridPhotoForPopup = null;
    private int selectedGridCellIndexForPopup = -1;
    private List<JSONObject> selectedGridDataListForPopup = null;
    private String selectedGridShapeForPopup = "ROUNDED";
    private int selectedGridCellSizeForPopup = 200;


    private ImageView selectedSwapCell = null;
    private int selectedSwapCellIdx = -1;
    private List<JSONObject> selectedSwapDataList = null;
    private RelativeLayout selectedSwapGrid = null;
    private String selectedSwapShape = "ROUNDED";
    private int selectedSwapCellSize = 200;


    private ActivityResultLauncher<Intent> framePickerLauncher;

    private static class GridMeta {
        int rows, cols, cellSizePx;
        String shape;
        boolean showName, showInfo;
        List<JSONObject> cellDataList;

        GridMeta(int rows, int cols, String shape, int cellSizePx,
                 boolean showName, boolean showInfo,
                 List<JSONObject> cellDataList) {
            this.rows = rows;
            this.cols = cols;
            this.shape = shape;
            this.cellSizePx = cellSizePx;
            this.showName = showName;
            this.showInfo = showInfo;
            this.cellDataList = cellDataList;
        }
    }


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rotationGestureDetector = new RotationGestureDetector(deltaAngle -> {
            if (currentlySelectedView != null && currentlySelectedView != main_image_view) {
                float current = currentlySelectedView.getRotation();
                currentlySelectedView.setRotation(current + deltaAngle);
            }
        });


        View btnNext = findViewById(R.id.btn_next_page);
        View btnPrev = findViewById(R.id.btn_prev_page);

        mainLayout = findViewById(R.id.main_layout);
        main_image_view = findViewById(R.id.main_image_view);
        txtPageIndicator = findViewById(R.id.txt_page_indicator);
        btnMultiSelect = findViewById(R.id.bt_multiselect);

        drawerLayout = findViewById(R.id.drawer_layout);
        rvLockedLayers = findViewById(R.id.rv_locked_layers);
        tvLockCount = findViewById(R.id.tv_lock_count);
        tvLockEmpty = findViewById(R.id.tv_lock_empty);


        tabAllLayers = findViewById(R.id.tab_all_layers);
        tabLockedLayers = findViewById(R.id.tab_locked_layers);
        tabUnlockedLayers = findViewById(R.id.tab_unlocked_layers);
        LinearLayout parentLayout = findViewById(R.id.bottom_controls);

        View includeMultiSelect = findViewById(R.id.include_multi_select_toolbar);
        if (includeMultiSelect != null) {
            multiSelectToolbar = includeMultiSelect.findViewById(R.id.multi_select_toolbar);

            // ── Include ની default visibility GONE
            includeMultiSelect.setVisibility(View.GONE);
        }


        if (multiSelectToolbar == null) android.util.Log.e("NULL_CHECK", "multiSelectToolbar NULL");
        if (drawerLayout == null) android.util.Log.e("NULL_CHECK", "drawerLayout NULL");
        if (rvLockedLayers == null) android.util.Log.e("NULL_CHECK", "rvLockedLayers NULL");


        for (int i = 0; i < parentLayout.getChildCount(); i++) {
            View child = parentLayout.getChildAt(i);
            child.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    scrollToCenter(v);
                    // અહિયાં તમે ID મુજબ અલગ અલગ કામ કરાવી શકો
                    handleButtonClick(v);
                }
            });
        }

        // ── Open button
        findViewById(R.id.btn_open_lock_panel).setOnClickListener(v -> drawerLayout.openDrawer(androidx.core.view.GravityCompat.START));

// ── Close button
        findViewById(R.id.btn_close_lock_panel).setOnClickListener(v -> drawerLayout.closeDrawer(androidx.core.view.GravityCompat.START));

// ── Unlock All
        findViewById(R.id.btn_unlock_all).setOnClickListener(v -> {
            new android.app.AlertDialog.Builder(this).setTitle("Unlock All").setMessage("બધા locked layers unlock કરવા?").setPositiveButton("Unlock All", (d, w) -> {
                unlockAllLayers();
            }).setNegativeButton("Cancel", null).show();
        });

        tabAllLayers.setOnClickListener(v -> {
            setActiveTab(0);
            lockedLayersAdapter.setFilterMode(LockedLayersAdapter.FilterMode.ALL);
            refreshLockedLayersPanel();
        });

        tabLockedLayers.setOnClickListener(v -> {
            setActiveTab(1);
            lockedLayersAdapter.setFilterMode(LockedLayersAdapter.FilterMode.LOCKED);
            refreshLockedLayersPanel();
        });

        tabUnlockedLayers.setOnClickListener(v -> {
            setActiveTab(2);
            lockedLayersAdapter.setFilterMode(LockedLayersAdapter.FilterMode.UNLOCKED);
            refreshLockedLayersPanel();
        });


// ── RecyclerView setup
        lockedLayersAdapter = new LockedLayersAdapter();
        rvLockedLayers.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(this));
        rvLockedLayers.setAdapter(lockedLayersAdapter);

        lockedLayersAdapter.setOnLayerActionListener(new LockedLayersAdapter.OnLayerActionListener() {

            @Override
            public void onLock(View view, int position) {
                if (!lockedViews.contains(view)) {
                    toggleLock(view);
                }
                refreshLockedLayersPanel();
            }

            @Override
            public void onUnlock(View view, int position) {
                if (lockedViews.contains(view)) {
                    toggleLock(view);
                }
                refreshLockedLayersPanel();
                if (lockedViews.isEmpty()) {
                    drawerLayout.closeDrawer(androidx.core.view.GravityCompat.START);
                }
            }

            @Override
            public void onSelect(View view, int position) {
                drawerLayout.closeDrawer(androidx.core.view.GravityCompat.START);

                view.post(() -> {
                    view.setAlpha(0.2f);
                    view.postDelayed(() -> view.setAlpha(0.85f), 150);
                    view.postDelayed(() -> view.setAlpha(0.2f), 300);
                    view.postDelayed(() -> view.setAlpha(0.85f), 450);
                    view.postDelayed(() -> view.setAlpha(lockedViews.contains(view) ? 0.85f : 1.0f), 600);
                });
            }

            // ✅ આ method ઉમેરો — error fix થશે
            @Override
            public void onRestore(View view, int position) {
                Object tag = view.getTag(R.id.btn_ms_select_all);
                if (tag == null) return;

                String tagStr = tag.toString();
                if (!tagStr.startsWith("DELETED_TEXT_")) return;

                try {
                    int deletedIndex = Integer.parseInt(tagStr.replace("DELETED_TEXT_", ""));

                    if (deletedIndex < 0 || deletedIndex >= deletedTextsList.size()) return;

                    org.json.JSONObject obj = deletedTextsList.get(deletedIndex);

                    addNewTextViewFromLoad(obj);
                    deletedTextsList.remove(deletedIndex);
                    exportToJson();
                    refreshLockedLayersPanel();

                    android.widget.Toast.makeText(MainActivity.this, "✅ Restore થઈ ગયું!", android.widget.Toast.LENGTH_SHORT).show();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onMoveUp(View view, int position) {
                Object tag = view.getTag(R.id.btn_ms_select_all);
                if (tag != null && tag.toString().startsWith("DELETED_TEXT_")) return;
                bringViewOneLayerUp(view);
                exportToJson();
                // ✅ Slight delay — layout update પછી refresh
                mainLayout.post(() -> refreshLockedLayersPanel());
            }

            @Override
            public void onMoveDown(View view, int position) {
                Object tag = view.getTag(R.id.btn_ms_select_all);
                if (tag != null && tag.toString().startsWith("DELETED_TEXT_")) return;
                sendViewOneLayerDown(view);
                exportToJson();
                mainLayout.post(() -> refreshLockedLayersPanel());
            }
        });


        // XML થી bind


        btnMultiSelect.setOnLongClickListener(v -> {
            if (isMultiSelectMode && !selectedViews.isEmpty()) {
                for (View sel : selectedViews) restoreViewBorder(sel);
                selectedViews.clear();
                groupStartPositions.clear();
                updateMultiSelectBtnLabel();
                Toast.makeText(this, "બધા deselect થયા", Toast.LENGTH_SHORT).show();
            }
            return true;
        });

        btnMultiSelect.setOnClickListener(v -> {
            if (!isMultiSelectMode) {
                isMultiSelectMode = true;
                deselectAll();
                selectedOriginalSizes.clear();

                // ✅ Include hide — popup use કરશે
                View includeView = findViewById(R.id.include_multi_select_toolbar);
                if (includeView != null) {
                    includeView.setVisibility(View.GONE);
                }

                showMultiSelectPopup(); // ✅ Movable popup

                updateMultiSelectBtnLabel();
                Toast.makeText(this, "Elements tap = select", Toast.LENGTH_LONG).show();
            } else {
                exitMultiSelectMode();
            }
        });

        setupMultiSelectToolbar();


        // Only one empty page
        allPagesData.add(createEmptyPage());
        currentPageIndex = 0;
        updatePageIndicator();

        scaleGestureDetector = new ScaleGestureDetector(this, new ScaleListener());

        mainLayout.setOnTouchListener((v, event) -> {

            // mainLayout.setOnTouchListener માં ઉમેરો:
            if (isGridSwapMode) {
                isGridSwapMode = false;
                resetSwapSelection();
                Toast.makeText(this, "Swap mode OFF", Toast.LENGTH_SHORT).show();
            }

            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (currentlySelectedGrid != null) {
                    return true;
                }

                deselectAll();
                hideAllPopupWindows();
            }
            if (event.getAction() == MotionEvent.ACTION_UP) {

                // ✅ આ ઉમેરો
                try {
                    if (frameImageControlsPopup != null && frameImageControlsPopup.isShowing()) {
                        frameImageControlsPopup.dismiss();
                    }
                } catch (Exception ignored) {
                }

                deselectAll();
                hideAllPopupWindows();
            }
            return true;

        });

        btnNext.setOnClickListener(v -> {
            if (currentPageIndex < allPagesData.size() - 1) {
                saveCurrentPage();
                currentPageIndex++;
                loadPageData(allPagesData.get(currentPageIndex));
                updatePageIndicator();

                // ✅ New page = locked list refresh
                mainLayout.post(() -> {
                    lockedViews.clear(); // new page = fresh lock state
                    refreshLockedLayersPanel();
                });
            }
        });

        btnPrev.setOnClickListener(v -> {
            if (currentPageIndex > 0) {
                saveCurrentPage();
                currentPageIndex--;
                loadPageData(allPagesData.get(currentPageIndex));
                updatePageIndicator();

                // ✅ New page = locked list refresh
                mainLayout.post(() -> {
                    lockedViews.clear();
                    refreshLockedLayersPanel();
                });
            }
        });

        String filePath = getIntent().getStringExtra("FILE_PATH");
        if (filePath != null) {
            currentOpenFilePath = filePath;
            mainLayout.post(() -> importFromJson(filePath));
        }

        //String loadedJson = getIntent().getStringExtra("loaded_json");

        String loadedJson = getIntent().getStringExtra("loaded_json");
        Log.d("DEBUG_JSON", "Received JSON: " + loadedJson);

        if (loadedJson != null && !loadedJson.isEmpty()) {
            // String content directly parse karo - file path nahi
            mainLayout.post(() -> importFromJsonString(loadedJson));
        }

        // ── MainActivity.java — framePickerLauncher update
// adjustedMergedPath handle ઉમેરો

        framePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Intent data = result.getData();

                        String frameUrl = data.getStringExtra(FramePickerActivity.EXTRA_FRAME_URL);
                        String maskUrl = data.getStringExtra(FramePickerActivity.EXTRA_MASK_URL);
                        String imageTop = data.getStringExtra(FramePickerActivity.EXTRA_IMAGE_TOP_URL);
                        int overlayColor = data.getIntExtra(FramePickerActivity.EXTRA_OVERLAY_COLOR, Color.TRANSPARENT);
                        boolean hasPhoto = data.getBooleanExtra(FramePickerActivity.EXTRA_HAS_PHOTO, false);

                        // ── Store pending
                        pendingFrameUrl = frameUrl;
                        pendingMaskUrl = maskUrl;
                        pendingImageTopUrl = imageTop;
                        pendingFrameOverlayColor = overlayColor;

                        // ── Case 1: Photo Adjust activity result — merged file
                        String adjustedPath = data.getStringExtra("adjustedMergedPath");
                        String adjustedMaskPath = data.getStringExtra("adjustedUserMaskedPath");

                        if (hasPhoto && adjustedPath != null && !adjustedPath.isEmpty()) {

                            // ── Adjusted bitmap already merged — directly use
                            Bitmap mergedBmp = BitmapFactory.decodeFile(adjustedPath);
                            Bitmap maskedBmp = adjustedMaskPath != null
                                    ? BitmapFactory.decodeFile(adjustedMaskPath) : null;

                            if (mergedBmp != null) {
                                addMergedFrameToLayout(mergedBmp, imageTop, maskedBmp);
                            } else {
                                Toast.makeText(this, "Image load failed", Toast.LENGTH_SHORT).show();
                            }

                            // ── Case 2: Raw photo URI (no adjust done)
                        } else if (hasPhoto) {
                            String photoUriStr = data.getStringExtra(FramePickerActivity.EXTRA_USER_PHOTO_URI);
                            if (photoUriStr != null) {
                                // ── Normal UCrop flow
                                Uri photoUri = Uri.parse(photoUriStr);
                                Glide.with(this).asBitmap().load(photoUri)
                                        .into(new CustomTarget<Bitmap>() {
                                            @Override
                                            public void onResourceReady(@NonNull Bitmap bmp,
                                                                        @Nullable Transition<? super Bitmap> t) {
                                                try {
                                                    File f = new File(getCacheDir(),
                                                            "frame_photo_" + System.currentTimeMillis() + ".jpg");
                                                    FileOutputStream fos = new FileOutputStream(f);
                                                    bmp.compress(Bitmap.CompressFormat.JPEG, 95, fos);
                                                    fos.flush();
                                                    fos.close();
                                                    mergeImageWithFrame(Uri.fromFile(f),
                                                            maskUrl, imageTop);
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            }

                                            @Override
                                            public void onLoadCleared(@Nullable Drawable p) {
                                            }
                                        });
                            }

                            // ── Case 3: No photo — frame only
                        } else {
                            addFrameWithoutPhoto(frameUrl, maskUrl, imageTop, overlayColor);
                        }
                    }
                }
        );
    }


    // ─────────────────────────────────────────────────────────
    private void addFrameWithoutPhotoAndBorder(
            String frameUrl, String maskUrl, String topUrl,
            int overlayColor, int borderColor, int borderWidth) {

        // Existing addFrameWithoutPhoto() ને call કરો
        // border apply — frame add પછી ImageView ઉપર
        addFrameWithoutPhoto(frameUrl, maskUrl, topUrl, overlayColor);

        // ── Border apply — post delay માં (ImageView add થઈ ગઈ પછી)
        if (borderWidth > 0 && borderColor != Color.TRANSPARENT) {
            mainLayout.postDelayed(() -> {
                // ── Last added FRAMED_IMAGE ImageView find
                for (int i = mainLayout.getChildCount() - 1; i >= 0; i--) {
                    View v = mainLayout.getChildAt(i);
                    Object tag = v.getTag(R.id.btn_set_background);
                    if ("FRAMED_IMAGE".equals(tag) && v instanceof android.widget.ImageView) {
                        applyBorderToFrameView((android.widget.ImageView) v,
                                borderColor, borderWidth);
                        break;
                    }
                }
            }, 500);
        }
    }


    private android.widget.ImageView addMergedFrameToLayoutAndReturn(
            android.graphics.Bitmap mergedBitmap,
            String frameUrl,
            android.graphics.Bitmap userMaskedBitmap) {

        if (mergedBitmap == null) return null;

        final android.widget.ImageView framedImage = new android.widget.ImageView(this);
        framedImage.setImageBitmap(mergedBitmap);
        framedImage.setScaleType(android.widget.ImageView.ScaleType.FIT_CENTER);
        framedImage.setBackgroundColor(android.graphics.Color.TRANSPARENT);

        framedImage.setTag(R.id.btn_set_background, "FRAMED_IMAGE");
        framedImage.setTag(R.id.btn_sticker_gallery, frameUrl);
        framedImage.setTag(R.id.btn_location, pendingMaskUrl);
        framedImage.setTag(R.id.btn_add_sticker, pendingImageTopUrl);
        framedImage.setTag(R.id.seek_multi_size, pendingFrameOverlayColor);
        framedImage.setTag(R.id.tv_size_label, userMaskedBitmap);

        int size = (int) (Math.min(mainLayout.getWidth(), mainLayout.getHeight()) * 0.8f);
        if (size <= 0) size = 600;

        android.widget.RelativeLayout.LayoutParams params =
                new android.widget.RelativeLayout.LayoutParams(size, size);
        params.addRule(android.widget.RelativeLayout.CENTER_IN_PARENT);
        framedImage.setLayoutParams(params);

        applyTouchListenerForSticker(framedImage);
        mainLayout.addView(framedImage);
        selectView(framedImage);

        Toast.makeText(this, "✅ Photo Frame add થઈ ગયો!", Toast.LENGTH_SHORT).show();

        return framedImage;
    }

    // ─────────────────────────────────────────────────────────
// ── Border apply to ImageView
// ─────────────────────────────────────────────────────────
    private void applyBorderToFrameView(android.widget.ImageView iv,
                                        int borderColor, int borderWidth) {
        if (borderWidth <= 0 || borderColor == android.graphics.Color.TRANSPARENT) {
            iv.setPadding(0, 0, 0, 0);
            return;
        }

        // ── GradientDrawable border
        android.graphics.drawable.GradientDrawable gd =
                new android.graphics.drawable.GradientDrawable();
        gd.setColor(android.graphics.Color.TRANSPARENT);
        gd.setStroke(borderWidth, borderColor);
        gd.setCornerRadius(0f);
        iv.setBackground(gd);

        // ── Padding = border width (border visible karvanu)
        iv.setPadding(borderWidth, borderWidth, borderWidth, borderWidth);

        exportToJson();
    }


    private void addFrameWithPhotoAndBorder(
            String frameUrl, String maskUrl, String topUrl,
            int overlayColor, Uri photoUri,
            int borderColor, int borderWidth) {

        Toast.makeText(this, "Frame + Photo apply થઈ રહ્યો છે...", Toast.LENGTH_SHORT).show();

        // ── Load mask bitmap
        Glide.with(this).asBitmap()
                .load(maskUrl.isEmpty() ? topUrl : maskUrl)
                .into(new com.bumptech.glide.request.target.CustomTarget<android.graphics.Bitmap>() {

                    @Override
                    public void onResourceReady(@NonNull android.graphics.Bitmap maskBitmap,
                                                @Nullable com.bumptech.glide.request.transition.Transition<
                                                        ? super android.graphics.Bitmap> t) {

                        int w = maskBitmap.getWidth();
                        int h = maskBitmap.getHeight();

                        // ── Load user photo
                        Glide.with(MainActivity.this).asBitmap()
                                .load(photoUri)
                                .into(new com.bumptech.glide.request.target.CustomTarget<
                                        android.graphics.Bitmap>(w, h) {

                                    @Override
                                    public void onResourceReady(
                                            @NonNull android.graphics.Bitmap userBitmap,
                                            @Nullable com.bumptech.glide.request.transition.Transition<
                                                    ? super android.graphics.Bitmap> t2) {

                                        // ── Load top image
                                        Glide.with(MainActivity.this).asBitmap()
                                                .load(topUrl)
                                                .into(new com.bumptech.glide.request.target.CustomTarget<
                                                        android.graphics.Bitmap>(w, h) {

                                                    @Override
                                                    public void onResourceReady(
                                                            @NonNull android.graphics.Bitmap topBitmap,
                                                            @Nullable com.bumptech.glide.request.transition.Transition<
                                                                    ? super android.graphics.Bitmap> t3) {

                                                        // ── Merge: user + mask + top
                                                        pendingFrameOverlayColor = overlayColor;
                                                        MergeResult mergeResult =
                                                                mergeThreeLayers(userBitmap, maskBitmap,
                                                                        topBitmap, w, h);

                                                        // ── Add to layout
                                                        android.widget.ImageView framedIv =
                                                                addMergedFrameToLayoutAndReturn(
                                                                        mergeResult.finalBitmap,
                                                                        topUrl,
                                                                        mergeResult.userMaskedBitmap);

                                                        // ── Border apply
                                                        if (framedIv != null && borderWidth > 0
                                                                && borderColor != Color.TRANSPARENT) {
                                                            applyBorderToFrameView(framedIv,
                                                                    borderColor, borderWidth);
                                                        }
                                                    }

                                                    @Override
                                                    public void onLoadCleared(@Nullable android.graphics.drawable.Drawable p) {
                                                    }

                                                    @Override
                                                    public void onLoadFailed(@Nullable android.graphics.drawable.Drawable e) {
                                                        // top ન મળ્યો — user+mask merge
                                                        pendingFrameOverlayColor = overlayColor;
                                                        MergeResult mergeResult =
                                                                mergeThreeLayers(userBitmap, maskBitmap,
                                                                        null, w, h);
                                                        android.widget.ImageView framedIv =
                                                                addMergedFrameToLayoutAndReturn(
                                                                        mergeResult.finalBitmap,
                                                                        maskUrl,
                                                                        mergeResult.userMaskedBitmap);
                                                        if (framedIv != null && borderWidth > 0
                                                                && borderColor != Color.TRANSPARENT) {
                                                            applyBorderToFrameView(framedIv,
                                                                    borderColor, borderWidth);
                                                        }
                                                    }
                                                });
                                    }

                                    @Override
                                    public void onLoadCleared(@Nullable android.graphics.drawable.Drawable p) {
                                    }
                                });
                    }

                    @Override
                    public void onLoadCleared(@Nullable android.graphics.drawable.Drawable p) {
                    }

                    @Override
                    public void onLoadFailed(@Nullable android.graphics.drawable.Drawable e) {
                        Toast.makeText(MainActivity.this,
                                "Frame load failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    // External storage thi aaveli JSON String directly parse karvo
    private void importFromJsonString(String jsonString) {
        try {
            JSONObject root = new JSONObject(jsonString);

            // main_pages array lo
            JSONArray mainPages = root.getJSONArray("main_pages");

            allPagesData.clear();

            for (int i = 0; i < mainPages.length(); i++) {
                allPagesData.add(mainPages.getJSONObject(i));
            }

            // deleted_pages hoy to e pan lo (optional)
            if (root.has("deleted_pages")) {
                JSONArray deletedPages = root.getJSONArray("deleted_pages");
                // Tari deletedPagesList hoy to:
                // deletedPagesList.clear();
                // for (int i = 0; i < deletedPages.length(); i++) {
                //     deletedPagesList.add(deletedPages.getJSONObject(i));
                // }
            }

            currentPageIndex = 0;

            if (!allPagesData.isEmpty()) {
                mainLayout.post(() -> {
                    loadPageData(allPagesData.get(0));
                    updatePageIndicator();
                });
            }

            Toast.makeText(this, "✅ Design loaded!", Toast.LENGTH_SHORT).show();
            Log.d("DEBUG_JSON", "Pages loaded: " + mainPages.length());

        } catch (JSONException e) {
            Log.e("DEBUG_JSON", "Parse error: " + e.getMessage());
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void selectGroupViews(int groupIndex) {
        if (groupIndex < 0 || groupIndex >= allGroups.size()) return;

        // ── Deselect previous
        for (View gv : selectedViews)
            restoreViewBorder(gv);
        selectedViews.clear();
        selectedOriginalSizes.clear(); // ✅ clear first
        groupStartPositions.clear();   // ✅ clear first

        List<View> g = allGroups.get(groupIndex);
        selectedGroupIndex = groupIndex;

        for (View gv : g) {
            if (gv.getParent() == mainLayout) {
                selectedViews.add(gv);
                applySelectionBorder(gv);

                // ✅ Size save — align + resize માટે જરૂરી
                saveOriginalSize(gv);

                // ✅ GroupStartPositions — move માટે
                groupStartPositions.add(new float[]{gv.getX(), gv.getY()});
            }
        }

        if (seekMultiSize != null) seekMultiSize.setProgress(50);
        if (tvSizeLabel != null) tvSizeLabel.setText("+0.0sp");

        updateMultiSelectBtnLabel();

        // ✅ MultiSelect mode ON
        if (!isMultiSelectMode) {
            isMultiSelectMode = true;
            showMultiSelectPopup();
        }

        Toast.makeText(this, "Group " + (groupIndex + 1) + " — " + selectedViews.size() + " elements selected", Toast.LENGTH_SHORT).show();
    }


    private void createGroupFromSelected() {
        if (selectedViews.size() < 2) {
            Toast.makeText(this, "ઓછામાં ઓછા 2 elements select કરો", Toast.LENGTH_SHORT).show();
            return;
        }

        // ── New group create
        List<View> newGroup = new ArrayList<>(selectedViews);
        allGroups.add(newGroup);
        int groupIndex = allGroups.size() - 1;

        // ── Tag set — દરેક view ને group index
        for (View v : newGroup) {
            v.setTag(R.id.seek_move_speed, "GROUP_" + groupIndex);
        }

        // ── Border — group color
        applyGroupBorder(newGroup, groupIndex);

        Toast.makeText(this, newGroup.size() + " elements group થયા! (Group " + (groupIndex + 1) + ")", Toast.LENGTH_SHORT).show();

        // ── Deselect
        selectedViews.clear();
        selectedOriginalSizes.clear();
        updateMultiSelectBtnLabel();

        exportToJson();
    }

    private void selectGroupByView(View tappedView) {
        // ── Group tag check
        Object tag = tappedView.getTag(R.id.seek_move_speed);
        if (tag == null || !tag.toString().startsWith("GROUP_")) {
            return;
        }

        // ── Group index get
        int groupIndex = Integer.parseInt(tag.toString().replace("GROUP_", ""));

        if (groupIndex < 0 || groupIndex >= allGroups.size()) return;

        // ── Deselect previous
        for (View v : selectedViews) restoreViewBorder(v);
        selectedViews.clear();
        selectedOriginalSizes.clear();

        // ── Group views select
        List<View> group = allGroups.get(groupIndex);
        selectedGroupIndex = groupIndex;

        for (View v : group) {
            // View still in layout check
            if (v.getParent() == mainLayout) {
                selectedViews.add(v);
                applySelectionBorder(v);
                saveOriginalSize(v);
            }
        }

        if (seekMultiSize != null) seekMultiSize.setProgress(50);
        if (tvSizeLabel != null) tvSizeLabel.setText("+0.0sp");

        updateMultiSelectBtnLabel();

        Toast.makeText(this, "Group " + (groupIndex + 1) + " selected (" + selectedViews.size() + " elements)", Toast.LENGTH_SHORT).show();
    }


    private void showGroupsDialog() {
        if (allGroups.isEmpty()) {
            Toast.makeText(this, "કોઈ group નથી", Toast.LENGTH_SHORT).show();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("📦 Groups (" + allGroups.size() + ")");

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(16, 16, 16, 16);

        ScrollView sv = new ScrollView(this);
        LinearLayout container = new LinearLayout(this);
        container.setOrientation(LinearLayout.VERTICAL);

        int[] groupColors = {Color.parseColor("#FF5722"), Color.parseColor("#9C27B0"), Color.parseColor("#009688"), Color.parseColor("#FF9800"), Color.parseColor("#E91E63"), Color.parseColor("#3F51B5"),};

        // ── Dialog reference — reopen માટે
        final AlertDialog[] dialogRef = {null};

        for (int i = 0; i < allGroups.size(); i++) {
            // ✅ Current index snapshot — lambda safe
            final int snapIdx = i;

            List<View> group = allGroups.get(i);

            // ── Valid views count
            int validCount = 0;
            for (View v : group) {
                if (v.getParent() == mainLayout) validCount++;
            }
            if (validCount == 0) continue;

            int color = groupColors[i % groupColors.length];

            // ── Row
            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setPadding(12, 12, 12, 12);
            row.setBackgroundColor(Color.WHITE);
            LinearLayout.LayoutParams rowP = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            rowP.setMargins(0, 0, 0, 4);
            row.setLayoutParams(rowP);

            // ── Color dot
            View dot = new View(this);
            GradientDrawable dotGd = new GradientDrawable();
            dotGd.setShape(GradientDrawable.OVAL);
            dotGd.setColor(color);
            LinearLayout.LayoutParams dotP = new LinearLayout.LayoutParams(20, 20);
            dotP.setMargins(0, 0, 10, 0);
            dotP.gravity = Gravity.CENTER_VERTICAL;
            dot.setLayoutParams(dotP);
            dot.setBackground(dotGd);
            row.addView(dot);

            // ── Label
            TextView label = new TextView(this);
            label.setText("Group " + (i + 1) + "  (" + validCount + " items)");
            label.setTextSize(13);
            label.setTextColor(Color.parseColor("#212121"));
            LinearLayout.LayoutParams labelP = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
            labelP.gravity = Gravity.CENTER_VERTICAL;
            label.setLayoutParams(labelP);
            row.addView(label);

            // ── Select button
            Button btnSelect = new Button(this);
            btnSelect.setText("✓");
            btnSelect.setTextSize(12);
            btnSelect.setTextColor(Color.WHITE);
            btnSelect.setBackgroundColor(color);
            LinearLayout.LayoutParams btnP = new LinearLayout.LayoutParams(80, LinearLayout.LayoutParams.WRAP_CONTENT);
            btnP.setMargins(4, 0, 4, 0);
            btnSelect.setLayoutParams(btnP);
            btnSelect.setOnClickListener(v -> {
                // ✅ Bounds check
                if (snapIdx >= allGroups.size()) {
                    Toast.makeText(this, "Group available નથી", Toast.LENGTH_SHORT).show();
                    return;
                }

                selectGroupViews(snapIdx);

                if (dialogRef[0] != null) dialogRef[0].dismiss();

                // ── Deselect previous
                for (View gv : selectedViews)
                    restoreViewBorder(gv);
                selectedViews.clear();
                selectedOriginalSizes.clear();

                // ── Group select
                List<View> g = allGroups.get(snapIdx);
                selectedGroupIndex = snapIdx;

                for (View gv : g) {
                    if (gv.getParent() == mainLayout) {
                        selectedViews.add(gv);
                        applySelectionBorder(gv);
                        saveOriginalSize(gv);
                    }
                }

                if (seekMultiSize != null) seekMultiSize.setProgress(50);
                if (tvSizeLabel != null) tvSizeLabel.setText("+0.0sp");
                updateMultiSelectBtnLabel();

                if (dialogRef[0] != null) dialogRef[0].dismiss();

                Toast.makeText(this, "Group " + (snapIdx + 1) + " selected!", Toast.LENGTH_SHORT).show();
            });
            row.addView(btnSelect);

            // ── Delete Group Views button
            Button btnDelete = new Button(this);
            btnDelete.setText("🗑");
            btnDelete.setTextSize(12);
            btnDelete.setTextColor(Color.WHITE);
            btnDelete.setBackgroundColor(Color.parseColor("#C62828"));
            btnDelete.setLayoutParams(btnP);
            btnDelete.setOnClickListener(v -> {
                // ✅ Bounds check
                if (snapIdx >= allGroups.size()) return;
                showGroupPreviewDialog(snapIdx);
                new AlertDialog.Builder(this).setTitle("Group Delete").setMessage("Group " + (snapIdx + 1) + " ના બધા elements delete કરવા?").setPositiveButton("Delete All", (d2, w2) -> {
                    // ✅ Bounds check again
                    if (snapIdx >= allGroups.size()) return;

                    List<View> g = allGroups.get(snapIdx);

                    // ── Layout માંથી remove
                    for (View gv : g) {
                        if (gv.getParent() == mainLayout) {
                            // ── Deleted list
                            if (gv instanceof StrokeTextView) {
                                try {
                                    StrokeTextView tv = (StrokeTextView) gv;
                                    JSONObject obj = new JSONObject();
                                    obj.put("text", tv.getText().toString());
                                    obj.put("color", tv.getCurrentTextColor());
                                    deletedTextsList.add(obj);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            mainLayout.removeView(gv);
                        }
                    }

                    // ── Group remove
                    allGroups.remove(snapIdx);

                    // ── Re-index remaining groups
                    reindexGroups();

                    // ── Selected reset
                    selectedViews.clear();
                    selectedOriginalSizes.clear();
                    updateMultiSelectBtnLabel();

                    exportToJson();

                    // ── Dialog refresh
                    if (dialogRef[0] != null) dialogRef[0].dismiss();

                    // ── Reopen if groups remain
                    if (!allGroups.isEmpty()) {
                        showGroupsDialog();
                    } else {
                        Toast.makeText(this, "✅ Group delete થઈ ગયો!", Toast.LENGTH_SHORT).show();
                    }
                }).setNegativeButton("Cancel", null).show();
            });
            row.addView(btnDelete);

            // ── Ungroup button
            Button btnUngroup = new Button(this);
            btnUngroup.setText("↗");
            btnUngroup.setTextSize(12);
            btnUngroup.setTextColor(Color.WHITE);
            btnUngroup.setBackgroundColor(Color.parseColor("#616161"));
            btnUngroup.setLayoutParams(btnP);
            btnUngroup.setOnClickListener(v -> {
                // ✅ Bounds check
                if (snapIdx >= allGroups.size()) return;

                List<View> g = allGroups.get(snapIdx);
                for (View gv : g) {
                    gv.setTag(R.id.seek_move_speed, null);
                    restoreViewBorder(gv);
                }
                allGroups.remove(snapIdx);

                // ── Re-index
                reindexGroups();

                exportToJson();

                if (dialogRef[0] != null) dialogRef[0].dismiss();

                if (!allGroups.isEmpty()) {
                    showGroupsDialog();
                } else {
                    Toast.makeText(this, "Group ungroup થઈ ગયો!", Toast.LENGTH_SHORT).show();
                }
            });
            row.addView(btnUngroup);

            container.addView(row);
        }

        sv.addView(container);
        root.addView(sv);

        builder.setView(root);
        builder.setNegativeButton("Close", null);

        dialogRef[0] = builder.show();
    }

    // ── Re-index all groups after remove
    private void reindexGroups() {
        for (int j = 0; j < allGroups.size(); j++) {
            for (View gv : allGroups.get(j)) {
                gv.setTag(R.id.seek_move_speed, "GROUP_" + j);
            }
            applyGroupBorder(allGroups.get(j), j);
        }
    }

    private void showGroupPreviewDialog(int groupIndex) {
        if (groupIndex < 0 || groupIndex >= allGroups.size()) return;

        List<View> group = allGroups.get(groupIndex);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Group " + (groupIndex + 1) + " — Elements");

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(16, 16, 16, 16);
        root.setBackgroundColor(Color.parseColor("#F5F5F5"));

        ScrollView sv = new ScrollView(this);
        LinearLayout container = new LinearLayout(this);
        container.setOrientation(LinearLayout.VERTICAL);
        container.setPadding(8, 8, 8, 8);

        final AlertDialog[] previewDialog = {null};

        for (int i = 0; i < group.size(); i++) {
            final int idx = i;
            View gv = group.get(i);

            if (gv.getParent() != mainLayout) continue;

            // ── Item row
            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setPadding(12, 12, 12, 12);
            row.setBackgroundColor(Color.WHITE);
            LinearLayout.LayoutParams rowP = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            rowP.setMargins(0, 0, 0, 6);
            row.setLayoutParams(rowP);

            // ── Thumbnail
            if (gv instanceof ImageView) {
                // ── Image thumbnail
                ImageView thumb = new ImageView(this);
                LinearLayout.LayoutParams thumbP = new LinearLayout.LayoutParams(100, 100);
                thumbP.setMargins(0, 0, 12, 0);
                thumb.setLayoutParams(thumbP);
                thumb.setScaleType(ImageView.ScaleType.CENTER_CROP);
                thumb.setBackgroundColor(Color.parseColor("#EEEEEE"));

                // ── Image load
                Object uriTag = gv.getTag(R.id.btn_set_background);
                String uri = uriTag != null ? uriTag.toString() : "";

                if (uri.startsWith("http")) {
                    Glide.with(this).load(uri).into(thumb);
                } else if (uri.startsWith("file://")) {
                    Glide.with(this).load(new java.io.File(uri.replace("file://", ""))).into(thumb);
                } else if ("FRAMED_IMAGE".equals(uri)) {
                    // ── Frame — current bitmap show
                    thumb.setImageDrawable(((ImageView) gv).getDrawable());
                } else if ("LOCATION_ICON".equals(uri)) {
                    thumb.setImageResource(R.drawable.location_9);
                } else {
                    thumb.setImageResource(android.R.drawable.ic_menu_gallery);
                }
                row.addView(thumb);

            } else if (gv instanceof StrokeTextView) {
                // ── Text preview box
                StrokeTextView textPreview = new StrokeTextView(this);
                StrokeTextView src = (StrokeTextView) gv;
                textPreview.setText(src.getText());
                textPreview.setTextColor(src.getCurrentTextColor());
                textPreview.setTextSize(14);
                textPreview.setPadding(8, 8, 8, 8);
                textPreview.setBackgroundColor(Color.parseColor("#E3F2FD"));
                LinearLayout.LayoutParams textP = new LinearLayout.LayoutParams(100, 100);
                textP.setMargins(0, 0, 12, 0);
                textPreview.setLayoutParams(textP);
                row.addView(textPreview);
            }

            // ── Info
            LinearLayout info = new LinearLayout(this);
            info.setOrientation(LinearLayout.VERTICAL);
            LinearLayout.LayoutParams infoP = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
            infoP.gravity = Gravity.CENTER_VERTICAL;
            info.setLayoutParams(infoP);

            // ── Type label
            TextView typeLabel = new TextView(this);
            if (gv instanceof ImageView) {
                Object uriTag = gv.getTag(R.id.btn_set_background);
                String uri = uriTag != null ? uriTag.toString() : "";
                if ("FRAMED_IMAGE".equals(uri)) typeLabel.setText("🖼 Frame Image");
                else if ("LOCATION_ICON".equals(uri)) typeLabel.setText("📍 Location");
                else typeLabel.setText("🖼 Sticker " + (idx + 1));
            } else {
                StrokeTextView tv = (StrokeTextView) gv;
                typeLabel.setText("📝 " + tv.getText().toString());
            }
            typeLabel.setTextSize(13);
            typeLabel.setTextColor(Color.parseColor("#212121"));
            info.addView(typeLabel);

            // ── Position info
            TextView posLabel = new TextView(this);
            posLabel.setText("X: " + (int) gv.getX() + "  Y: " + (int) gv.getY());
            posLabel.setTextSize(11);
            posLabel.setTextColor(Color.GRAY);
            info.addView(posLabel);

            // ── Size info
            TextView sizeLabel = new TextView(this);
            sizeLabel.setText("W: " + gv.getWidth() + "  H: " + gv.getHeight());
            sizeLabel.setTextSize(11);
            sizeLabel.setTextColor(Color.GRAY);
            info.addView(sizeLabel);

            row.addView(info);

            // ── Action buttons
            LinearLayout btnCol = new LinearLayout(this);
            btnCol.setOrientation(LinearLayout.VERTICAL);

            // ── Select this element
            Button btnSelect = new Button(this);
            btnSelect.setText("✓ Select");
            btnSelect.setTextSize(11);
            btnSelect.setTextColor(Color.WHITE);
            btnSelect.setBackgroundColor(Color.parseColor("#1565C0"));
            LinearLayout.LayoutParams bP = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            bP.setMargins(0, 0, 0, 4);
            btnSelect.setLayoutParams(bP);
            btnSelect.setOnClickListener(v -> {
                // ── Single element select
                for (View sv2 : selectedViews)
                    restoreViewBorder(sv2);
                selectedViews.clear();
                selectedOriginalSizes.clear();

                selectedViews.add(gv);
                applySelectionBorder(gv);
                saveOriginalSize(gv);

                if (!isMultiSelectMode) {
                    isMultiSelectMode = true;
                    showMultiSelectPopup();
                }

                if (previewDialog[0] != null) previewDialog[0].dismiss();

                Toast.makeText(this, "Element selected!", Toast.LENGTH_SHORT).show();
            });
            btnCol.addView(btnSelect);

            // ── Remove from group
            Button btnRemove = new Button(this);
            btnRemove.setText("✕ Remove");
            btnRemove.setTextSize(11);
            btnRemove.setTextColor(Color.WHITE);
            btnRemove.setBackgroundColor(Color.parseColor("#C62828"));
            btnRemove.setLayoutParams(bP);
            btnRemove.setOnClickListener(v -> {
                // ── Group માંથી remove (delete નહીં)
                group.remove(gv);
                gv.setTag(R.id.seek_move_speed, null);
                restoreViewBorder(gv);

                // ── Group empty check
                if (group.isEmpty()) {
                    allGroups.remove(groupIndex);
                    reindexGroups();
                }

                exportToJson();

                if (previewDialog[0] != null) previewDialog[0].dismiss();

                // ── Reopen if group still exists
                if (!group.isEmpty()) {
                    showGroupPreviewDialog(groupIndex < allGroups.size() ? groupIndex : 0);
                }

                Toast.makeText(this, "Element group માંથી remove થઈ ગયો!", Toast.LENGTH_SHORT).show();
            });
            btnCol.addView(btnRemove);

            row.addView(btnCol);
            container.addView(row);
        }

        // ── Select All button
        Button btnSelectAll = new Button(this);
        btnSelectAll.setText("✓ Select All");
        btnSelectAll.setTextColor(Color.WHITE);
        btnSelectAll.setBackgroundColor(Color.parseColor("#1565C0"));
        LinearLayout.LayoutParams saP = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        saP.setMargins(0, 8, 0, 4);
        btnSelectAll.setLayoutParams(saP);
        btnSelectAll.setOnClickListener(v -> {
            selectGroupViews(groupIndex);
            if (previewDialog[0] != null) previewDialog[0].dismiss();
        });

        sv.addView(container);
        root.addView(sv);
        root.addView(btnSelectAll);

        builder.setView(root);
        builder.setNegativeButton("Close", null);
        previewDialog[0] = builder.show();
    }


    private void applyGroupBorder(List<View> group, int groupIndex) {
        // ── 6 group colors
        int[] groupColors = {Color.parseColor("#FF5722"), // Orange
                Color.parseColor("#9C27B0"), // Purple
                Color.parseColor("#009688"), // Teal
                Color.parseColor("#FF9800"), // Amber
                Color.parseColor("#E91E63"), // Pinkc
                Color.parseColor("#3F51B5"), // Indigo
        };
        int color = groupColors[groupIndex % groupColors.length];

        for (View v : group) {
            GradientDrawable gd = new GradientDrawable();
            gd.setColor(Color.TRANSPARENT);
            gd.setStroke(5, color);
            gd.setCornerRadius(8f);
            v.setBackground(gd);
            v.setPadding(8, 8, 8, 8);
        }
    }


    private void showMultiSelectPopup() {
        // ── Existing dismiss
        if (multiSelectPopup != null && multiSelectPopup.isShowing()) {
            multiSelectPopup.dismiss();
        }

        // ── Inflate same layout
        View includeView = findViewById(R.id.include_multi_select_toolbar);
        if (includeView == null) return;

        // ── New popup view — same XML inflate
        LayoutInflater inflater = LayoutInflater.from(this);
        View popupView = inflater.inflate(R.layout.layout_multi_select_toolbar, null);

        multiSelectPopup = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, false);
        multiSelectPopup.setOutsideTouchable(false);
        multiSelectPopup.setTouchable(true);
        multiSelectPopup.setBackgroundDrawable(new android.graphics.drawable.ColorDrawable(Color.TRANSPARENT));

        // ── Drag handle — popup move
        View dragHandle = popupView.findViewById(R.id.drag_handle);
        final int[] popupX = {100};
        final int[] popupY = {200};
        final float[] lastTX = {0};
        final float[] lastTY = {0};

        if (dragHandle != null) {
            dragHandle.setOnTouchListener((v2, event) -> {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        lastTX[0] = event.getRawX();
                        lastTY[0] = event.getRawY();
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        float dx = event.getRawX() - lastTX[0];
                        float dy = event.getRawY() - lastTY[0];
                        popupX[0] += (int) dx;
                        popupY[0] += (int) dy;
                        lastTX[0] = event.getRawX();
                        lastTY[0] = event.getRawY();
                        multiSelectPopup.update(popupX[0], popupY[0], -1, -1);
                        return true;
                }
                return false;
            });
        }

        // ── Buttons bind — same as setupMultiSelectToolbar
        bindMultiSelectButtons(popupView);

        // ── Show at center bottom
        multiSelectPopup.showAtLocation(mainLayout, Gravity.NO_GRAVITY, popupX[0], popupY[0]);
    }

    private void bindMultiSelectButtons(View root) {

        // ── seekbar
        seekMultiSize = root.findViewById(R.id.seek_multi_size);
        tvSizeLabel = root.findViewById(R.id.tv_size_label);
        seekMoveSpeed = root.findViewById(R.id.seek_move_speed);
        tvMoveSpeed = root.findViewById(R.id.tv_move_speed);

        btnMsSelectAll = root.findViewById(R.id.btn_ms_select_all);
        btnMsUnselect = root.findViewById(R.id.btn_ms_unselect);
        btnMsCopy = root.findViewById(R.id.btn_ms_copy);
        btnMsCancel = root.findViewById(R.id.btn_ms_cancel);
        btnMsSizeMinus = root.findViewById(R.id.btn_ms_size_minus);
        btnMsSizePlus = root.findViewById(R.id.btn_ms_size_plus);
        btnMsDelete = root.findViewById(R.id.btn_ms_delete);
        btnMsUp = root.findViewById(R.id.btn_ms_up);
        btnMsDown = root.findViewById(R.id.btn_ms_down);
        btnMsLeft = root.findViewById(R.id.btn_ms_left);
        btnMsRight = root.findViewById(R.id.btn_ms_right);
        btnSpeedSlow = root.findViewById(R.id.btn_speed_slow);
        btnSpeedFast = root.findViewById(R.id.btn_speed_fast);


        TextView btnAlignLeft = root.findViewById(R.id.btn_ms_align_left);
        TextView btnAlignCenter = root.findViewById(R.id.btn_ms_align_center);
        TextView btnAlignRight = root.findViewById(R.id.btn_ms_align_right);
        TextView btnAlignTop = root.findViewById(R.id.btn_ms_align_top);
        TextView btnAlignMiddle = root.findViewById(R.id.btn_ms_align_middle);
        TextView btnAlignBottom = root.findViewById(R.id.btn_ms_align_bottom);
        TextView btnDistributeH = root.findViewById(R.id.btn_ms_distribute_h);
        TextView btnDistributeV = root.findViewById(R.id.btn_ms_distribute_v);

        if (btnAlignLeft != null) btnAlignLeft.setOnClickListener(v -> alignSelectedViews("LEFT"));
        if (btnAlignCenter != null)
            btnAlignCenter.setOnClickListener(v -> alignSelectedViews("CENTER_H"));
        if (btnAlignRight != null)
            btnAlignRight.setOnClickListener(v -> alignSelectedViews("RIGHT"));
        if (btnAlignTop != null) btnAlignTop.setOnClickListener(v -> alignSelectedViews("TOP"));
        if (btnAlignMiddle != null)
            btnAlignMiddle.setOnClickListener(v -> alignSelectedViews("CENTER_V"));
        if (btnAlignBottom != null)
            btnAlignBottom.setOnClickListener(v -> alignSelectedViews("BOTTOM"));
        if (btnDistributeH != null)
            btnDistributeH.setOnClickListener(v -> alignSelectedViews("DISTRIBUTE_H"));
        if (btnDistributeV != null)
            btnDistributeV.setOnClickListener(v -> alignSelectedViews("DISTRIBUTE_V"));

// ── Filter state
        /*      final String[] currentFilter = {"ALL"};*/

// ── UI update helper

        TextView btnFilterAll = root.findViewById(R.id.btn_ms_filter_all);
        TextView btnFilterImage = root.findViewById(R.id.btn_ms_filter_image);
        TextView btnFilterText = root.findViewById(R.id.btn_ms_filter_text);

        Runnable updateFilterUI = () -> {
            if (btnFilterAll == null || btnFilterImage == null || btnFilterText == null) return;

            // Reset all
            btnFilterAll.setBackgroundColor(Color.parseColor("#E3F2FD"));
            btnFilterAll.setTextColor(Color.parseColor("#1565C0"));
            btnFilterImage.setBackgroundColor(Color.parseColor("#E3F2FD"));
            btnFilterImage.setTextColor(Color.parseColor("#1565C0"));
            btnFilterText.setBackgroundColor(Color.parseColor("#E3F2FD"));
            btnFilterText.setTextColor(Color.parseColor("#1565C0"));

            // Active highlight
            switch (currentFilter) {
                case "ALL":
                    btnFilterAll.setBackgroundColor(Color.parseColor("#1565C0"));
                    btnFilterAll.setTextColor(Color.WHITE);
                    break;
                case "IMAGE":
                    btnFilterImage.setBackgroundColor(Color.parseColor("#1565C0"));
                    btnFilterImage.setTextColor(Color.WHITE);
                    break;
                case "TEXT":
                    btnFilterText.setBackgroundColor(Color.parseColor("#1565C0"));
                    btnFilterText.setTextColor(Color.WHITE);
                    break;
            }
        };

        if (btnFilterAll != null) {
            btnFilterAll.setOnClickListener(v -> {
                currentFilter = "ALL";         // ✅ class field update
                updateFilterUI.run();
                applyFilterSelection("ALL");
            });
        }

        if (btnFilterImage != null) {
            btnFilterImage.setOnClickListener(v -> {
                currentFilter = "IMAGE";       // ✅
                updateFilterUI.run();
                applyFilterSelection("IMAGE");
            });
        }

        if (btnFilterText != null) {
            btnFilterText.setOnClickListener(v -> {
                currentFilter = "TEXT";        // ✅
                updateFilterUI.run();
                applyFilterSelection("TEXT");

                // ✅ Toast — text tap = edit
                Toast.makeText(MainActivity.this, "Text tap કરો = Edit popup ખુલશે", Toast.LENGTH_SHORT).show();
            });
        }


// ✅ currentFilter[0] → currentFilter
        if (btnFilterAll != null) {
            btnFilterAll.setOnClickListener(v -> {
                currentFilter = "ALL";
                updateFilterUI.run();
                applyFilterSelection("ALL");
            });
        }

        if (btnFilterImage != null) {
            btnFilterImage.setOnClickListener(v -> {
                currentFilter = "IMAGE";
                updateFilterUI.run();
                applyFilterSelection("IMAGE");
            });
        }

        if (btnFilterText != null) {
            btnFilterText.setOnClickListener(v -> {
                currentFilter = "TEXT";
                updateFilterUI.run();
                applyFilterSelection("TEXT");
                Toast.makeText(MainActivity.this, "Text tap = Edit popup ખુલશે", Toast.LENGTH_SHORT).show();
            });
        }
        // ── Cancel
        btnMsCancel.setOnClickListener(v -> exitMultiSelectMode());

        // ── Group button
        TextView btnGroup = root.findViewById(R.id.btn_ms_group);
        TextView btnShowGroups = root.findViewById(R.id.btn_ms_show_groups);

        if (btnGroup != null) {
            btnGroup.setOnClickListener(v -> createGroupFromSelected());
        }

        if (btnShowGroups != null) {
            btnShowGroups.setOnClickListener(v -> showGroupsDialog());
        }

        // ── Delete
        btnMsDelete.setOnClickListener(v -> {
            if (selectedViews.isEmpty()) {
                Toast.makeText(this, "પહેલા elements select કરો", Toast.LENGTH_SHORT).show();
                return;
            }
            new AlertDialog.Builder(this).setTitle("Delete Confirmation").setMessage(selectedViews.size() + " elements delete કરવા છો?").setPositiveButton("Delete", (d, w) -> deleteSelectedViews()).setNegativeButton("Cancel", null).show();
        });

        // ── Select All
        btnMsSelectAll.setOnClickListener(v -> {
            selectedViews.clear();
            selectedOriginalSizes.clear();
            for (int i = 0; i < mainLayout.getChildCount(); i++) {
                View child = mainLayout.getChildAt(i);
                if (child == main_image_view) continue;
                if (lockedViews.contains(child)) continue;
                if (child instanceof TextView || child instanceof ImageView) {
                    selectedViews.add(child);
                    applySelectionBorder(child);
                    saveOriginalSize(child);
                }
            }
            if (seekMultiSize != null) seekMultiSize.setProgress(50);
            if (tvSizeLabel != null) tvSizeLabel.setText("+0.0sp");
            updateMultiSelectBtnLabel();
        });

        // ── Unselect
        btnMsUnselect.setOnClickListener(v -> {
            for (View sel : selectedViews) restoreViewBorder(sel);
            selectedViews.clear();
            selectedOriginalSizes.clear();
            if (seekMultiSize != null) seekMultiSize.setProgress(50);
            if (tvSizeLabel != null) tvSizeLabel.setText("+0.0sp");
            updateMultiSelectBtnLabel();
        });

        // ── Copy
        btnMsCopy.setOnClickListener(v -> {
            if (selectedViews.isEmpty()) {
                Toast.makeText(this, "પહેલા elements select કરો", Toast.LENGTH_SHORT).show();
                return;
            }
            showCopyToPageDialog();
        });

        // ── Size
        btnMsSizeMinus.setOnClickListener(v -> {
            if (seekMultiSize == null) return;
            int nv = Math.max(0, seekMultiSize.getProgress() - 2);
            seekMultiSize.setProgress(nv);
            applyMultiSizeChange(nv);
        });
        btnMsSizePlus.setOnClickListener(v -> {
            if (seekMultiSize == null) return;
            int nv = Math.min(100, seekMultiSize.getProgress() + 2);
            seekMultiSize.setProgress(nv);
            applyMultiSizeChange(nv);
        });

        // ── Speed
        btnSpeedSlow.setOnClickListener(v -> {
            if (seekMoveSpeed == null) return;
            seekMoveSpeed.setProgress(0);
            currentMoveStep = SPEED_STEPS[0];
            if (tvMoveSpeed != null) tvMoveSpeed.setText(currentMoveStep + "px");
        });
        btnSpeedFast.setOnClickListener(v -> {
            if (seekMoveSpeed == null) return;
            seekMoveSpeed.setProgress(9);
            currentMoveStep = SPEED_STEPS[9];
            if (tvMoveSpeed != null) tvMoveSpeed.setText(currentMoveStep + "px");
        });

        // ── Arrow move
        btnMsUp.setOnClickListener(v -> moveSelectedViews(0, -currentMoveStep));
        btnMsDown.setOnClickListener(v -> moveSelectedViews(0, currentMoveStep));
        btnMsLeft.setOnClickListener(v -> moveSelectedViews(-currentMoveStep, 0));
        btnMsRight.setOnClickListener(v -> moveSelectedViews(currentMoveStep, 0));

        btnMsUp.setOnLongClickListener(v -> {
            moveSelectedViews(0, -currentMoveStep * 3);
            return true;
        });
        btnMsDown.setOnLongClickListener(v -> {
            moveSelectedViews(0, currentMoveStep * 3);
            return true;
        });
        btnMsLeft.setOnLongClickListener(v -> {
            moveSelectedViews(-currentMoveStep * 3, 0);
            return true;
        });
        btnMsRight.setOnLongClickListener(v -> {
            moveSelectedViews(currentMoveStep * 3, 0);
            return true;
        });

        // ── SeekBar listeners
        if (seekMultiSize != null) {
            seekMultiSize.setOnSeekBarChangeListener(new android.widget.SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(android.widget.SeekBar s, int progress, boolean fromUser) {
                    if (!fromUser) return;
                    applyMultiSizeChange(progress);
                }

                @Override
                public void onStartTrackingTouch(android.widget.SeekBar s) {
                }

                @Override
                public void onStopTrackingTouch(android.widget.SeekBar s) {
                }
            });
        }

        if (seekMoveSpeed != null) {
            seekMoveSpeed.setOnSeekBarChangeListener(new android.widget.SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(android.widget.SeekBar s, int progress, boolean fromUser) {
                    currentMoveStep = SPEED_STEPS[progress];
                    if (tvMoveSpeed != null) tvMoveSpeed.setText(currentMoveStep + "px");
                }

                @Override
                public void onStartTrackingTouch(android.widget.SeekBar s) {
                }

                @Override
                public void onStopTrackingTouch(android.widget.SeekBar s) {
                }
            });
        }
    }

    private void applyFilterSelection(String filter) {
        for (View v : selectedViews)
            restoreViewBorder(v);
        selectedViews.clear();
        selectedOriginalSizes.clear();

        for (int i = 0; i < mainLayout.getChildCount(); i++) {
            View child = mainLayout.getChildAt(i);
            if (child == main_image_view) continue;
            if (lockedViews.contains(child)) continue;

            boolean shouldSelect = false;

            switch (filter) {
                case "ALL":
                    shouldSelect = child instanceof TextView || child instanceof ImageView;
                    break;
                case "IMAGE":
                    // ✅ Only ImageView — StrokeTextView skip
                    shouldSelect = child instanceof ImageView && !(child instanceof StrokeTextView);
                    break;
                case "TEXT":
                    // ✅ Only StrokeTextView
                    shouldSelect = child instanceof StrokeTextView;
                    break;
            }

            if (shouldSelect) {
                selectedViews.add(child);
                applySelectionBorder(child);
                saveOriginalSize(child);
            }
        }

        if (seekMultiSize != null) seekMultiSize.setProgress(50);
        if (tvSizeLabel != null) tvSizeLabel.setText("+0.0sp");

        updateMultiSelectBtnLabel();

        Toast.makeText(this, filter + " — " + selectedViews.size() + " selected", Toast.LENGTH_SHORT).show();
    }


    private void unlockAllLayers() {
        List<View> toUnlock = new ArrayList<>(lockedViews);
        for (View v : toUnlock) {
            lockedViews.remove(v);
            v.setAlpha(1.0f);

            if (v instanceof StrokeTextView) {
                applyTouchListener((StrokeTextView) v);
                restoreTextBorder((StrokeTextView) v);
            } else if (v instanceof ImageView) {
                applyTouchListenerForSticker((ImageView) v);
                v.setBackground(null);
                v.setPadding(0, 0, 0, 0);
            }
        }

        refreshLockedLayersPanel();
        exportToJson();
        drawerLayout.closeDrawer(androidx.core.view.GravityCompat.START);

        Toast.makeText(this, "🔓 બધા unlock થઈ ગયા!", Toast.LENGTH_SHORT).show();
    }


    private void refreshLockedLayersPanel() {
        if (lockedLayersAdapter == null) return;

        List<View> layerViews = new ArrayList<>();
        List<String> layerLabels = new ArrayList<>();
        List<Boolean> layerStates = new ArrayList<>();

        // ── Current page ના views
        for (int i = 0; i < mainLayout.getChildCount(); i++) {
            View v = mainLayout.getChildAt(i);
            if (v == main_image_view) continue;

            String label;
            if (v instanceof StrokeTextView) {
                String txt = ((StrokeTextView) v).getText().toString();
                label = txt.isEmpty() ? "(Text " + (i + 1) + ")" : txt;
            } else if (v instanceof ImageView) {
                Object uriTag = v.getTag(R.id.btn_set_background);
                String uri = uriTag != null ? uriTag.toString() : "";
                if ("FRAMED_IMAGE".equals(uri)) label = "🖼 Frame Layer " + (i + 1);
                else if ("LOCATION_ICON".equals(uri)) label = "📍 Location " + (i + 1);
                else label = "🖼 Image " + (i + 1);
            } else {
                continue;
            }

            layerViews.add(v);
            layerLabels.add(label);
            layerStates.add(lockedViews.contains(v));
        }

        // ── ✅ Deleted texts પણ show કરો
        for (int i = 0; i < deletedTextsList.size(); i++) {
            JSONObject obj = deletedTextsList.get(i);
            String text = obj.optString("text", "(Deleted Text " + (i + 1) + ")");

            // ── Dummy view — restore button માટે index store
            TextView dummyView = new TextView(this);
            dummyView.setText(text);
            dummyView.setTag(R.id.btn_ms_select_all, "DELETED_TEXT_" + i); // index tag

            layerViews.add(dummyView);
            layerLabels.add("🗑 " + text);
            layerStates.add(false); // deleted = never locked
        }

        // ── Adapter update
        lockedLayersAdapter.updateLayers(layerViews, layerLabels, layerStates);

        // ... બાકીનો existing code same રહેશે
        int total = lockedLayersAdapter.getTotalCount();
        int locked = lockedLayersAdapter.getLockedCount();
        int unlocked = lockedLayersAdapter.getUnlockedCount();

        tvLockCount.setText("Total: " + total + "  🔒 " + locked + "  🔓 " + unlocked);

        tabAllLayers.setText("All (" + total + ")");
        tabLockedLayers.setText("🔒 (" + locked + ")");
        tabUnlockedLayers.setText("🔓 (" + unlocked + ")");

        boolean isEmpty = lockedLayersAdapter.getItemCount() == 0;
        tvLockEmpty.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        rvLockedLayers.setVisibility(isEmpty ? View.GONE : View.VISIBLE);

        TextView btnOpen = findViewById(R.id.btn_open_lock_panel);
        if (btnOpen != null) {
            btnOpen.setText(locked > 0 ? "🔒\n" + locked : "🔒");
            btnOpen.setBackgroundColor(locked > 0 ? Color.parseColor("#CC1565C0") : Color.parseColor("#88616161"));
        }
    }

    private void setActiveTab(int index) {
        // ── Reset all
        int activeColor = Color.parseColor("#1565C0");
        int inactiveColor = Color.parseColor("#F5F5F5");
        int activeText = Color.WHITE;
        int inactiveText = Color.parseColor("#888888");

        tabAllLayers.setBackgroundColor(index == 0 ? activeColor : inactiveColor);
        tabAllLayers.setTextColor(index == 0 ? activeText : inactiveText);

        tabLockedLayers.setBackgroundColor(index == 1 ? activeColor : inactiveColor);
        tabLockedLayers.setTextColor(index == 1 ? activeText : inactiveText);

        tabUnlockedLayers.setBackgroundColor(index == 2 ? activeColor : inactiveColor);
        tabUnlockedLayers.setTextColor(index == 2 ? activeText : inactiveText);
    }


    private void moveSelectedViews(float dx, float dy) {
        if (selectedViews.isEmpty()) {
            Toast.makeText(this, "પહેલા elements select કરો", Toast.LENGTH_SHORT).show();
            return;
        }

        float minX = main_image_view.getLeft();
        float maxX = main_image_view.getRight();
        float minY = main_image_view.getTop();
        float maxY = main_image_view.getBottom();

        for (View v : selectedViews) {
            float newX = v.getX() + dx;
            float newY = v.getY() + dy;

            // Boundary check
            newX = Math.max(minX, Math.min(newX, maxX - v.getWidth()));
            newY = Math.max(minY, Math.min(newY, maxY - v.getHeight()));

            v.setX(newX);
            v.setY(newY);
        }

        // groupStartPositions update — drag move sync
        groupStartPositions.clear();
        for (View sel : selectedViews) {
            groupStartPositions.add(new float[]{sel.getX(), sel.getY()});
        }
    }


    private void showCropOption(ImageView targetSticker) {
        // Tag check — Uri available?
        Object uriTag = targetSticker.getTag(R.id.btn_set_background);
        if (uriTag == null) return;

        String uriStr = uriTag.toString();
        if (uriStr.equals("FRAMED_IMAGE") || uriStr.equals("LOCATION_ICON")) return;

        pendingCropTarget = targetSticker;

        Intent intent = new Intent(this, ImageCropActivity.class);

        if (uriStr.startsWith("http")) {
            // ── URL → Glide cache file use
            Glide.with(this).asBitmap().load(uriStr).into(new CustomTarget<Bitmap>() {
                @Override
                public void onResourceReady(@NonNull Bitmap bmp, @Nullable Transition<? super Bitmap> t) {
                    try {
                        // ✅ PNG check — alpha હોય તો PNG save
                        boolean hasPng = bmp.hasAlpha();
                        String ext = hasPng ? ".png" : ".jpg";

                        File f = new File(getCacheDir(), "crop_src_" + System.currentTimeMillis() + ext);
                        FileOutputStream fos = new FileOutputStream(f);

                        if (hasPng) {
                            // ✅ PNG — alpha maintain
                            bmp.compress(Bitmap.CompressFormat.PNG, 100, fos);
                        } else {
                            bmp.compress(Bitmap.CompressFormat.JPEG, 95, fos);
                        }
                        fos.flush();
                        fos.close();

                        Intent i = new Intent(MainActivity.this, ImageCropActivity.class);
                        i.putExtra("imagePath", f.getAbsolutePath());
                        startActivityForResult(i, REQUEST_IMAGE_CROP);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onLoadCleared(@Nullable Drawable p) {
                }
            });
        } else {
            // ── Local file
            intent.putExtra("imagePath", uriStr.replace("file://", ""));
            startActivityForResult(intent, REQUEST_IMAGE_CROP);
        }
    }


    private void openFreehandCrop(ImageView targetSticker) {
        pendingFreehandTarget = targetSticker;

        Object uriTag = targetSticker.getTag(R.id.btn_set_background);
        if (uriTag == null) return;
        String uriStr = uriTag.toString();

        if (uriStr.startsWith("http")) {
            Glide.with(this).asBitmap().load(uriStr).into(new CustomTarget<Bitmap>() {
                @Override
                public void onResourceReady(@NonNull Bitmap bmp, @Nullable Transition<? super Bitmap> t) {
                    try {
                        boolean hasPng = bmp.hasAlpha();
                        String ext = hasPng ? ".png" : ".jpg";
                        File f = new File(getCacheDir(), "fh_src_" + System.currentTimeMillis() + ext);
                        FileOutputStream fos = new FileOutputStream(f);
                        if (hasPng) bmp.compress(Bitmap.CompressFormat.PNG, 100, fos);
                        else bmp.compress(Bitmap.CompressFormat.JPEG, 95, fos);
                        fos.flush();
                        fos.close();

                        Intent i = new Intent(MainActivity.this,

                                FreehandCropActivity.class);
                        i.putExtra("imagePath", f.getAbsolutePath());
                        startActivityForResult(i, REQUEST_FREEHAND_CROP);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onLoadCleared(@Nullable Drawable p) {
                }
            });
        } else {
            Intent i = new Intent(this, FreehandCropActivity.class);
            i.putExtra("imagePath", uriStr.replace("file://", ""));
            startActivityForResult(i, REQUEST_FREEHAND_CROP);
        }
    }


    private void deleteSelectedViews() {
        int count = selectedViews.size();

        for (View v : selectedViews) {
            // Text હોય તો deleted list માં add
            if (v instanceof StrokeTextView) {
                StrokeTextView tv = (StrokeTextView) v;
                try {
                    JSONObject deletedObj = new JSONObject();
                    deletedObj.put("text", tv.getText().toString());
                    deletedObj.put("color", tv.getCurrentTextColor());
                    deletedObj.put("size", tv.getTextSize() / getResources().getDisplayMetrics().scaledDensity);

                    float sw = mainLayout.getWidth();
                    float sh = mainLayout.getHeight();
                    deletedObj.put("xPercent", sw == 0 ? 0 : (tv.getX() / sw) * 100);
                    deletedObj.put("yPercent", sh == 0 ? 0 : (tv.getY() / sh) * 100);
                    deletedObj.put("bgColor", getStoredBackgroundColor(tv));
                    deletedObj.put("rotation", tv.getRotation());

                    Object borderTag = tv.getTag(R.id.btn_add_sticker);
                    deletedObj.put("borderStyle", borderTag instanceof Integer ? (int) borderTag : 0);

                    deletedObj.put("strokeWidth", tv.getStrokeWidth());
                    deletedObj.put("strokeColor", tv.getStrokeColor());
                    deletedObj.put("isBold", tv.getTypeface() != null && tv.getTypeface().isBold());
                    deletedObj.put("isUnderline", (tv.getPaintFlags() & Paint.UNDERLINE_TEXT_FLAG) != 0);

                    // Arc mode save
                    deletedObj.put("isArcMode", tv.isArcMode());
                    if (tv.isArcMode()) {
                        deletedObj.put("arcAngle", tv.getArcAngle());
                        deletedObj.put("arcRadius", tv.getRadius());
                        deletedObj.put("arcUp", tv.isArcUp());
                    }

                    deletedTextsList.add(deletedObj);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            // Layout માંથી remove
            mainLayout.removeView(v);
        }

        // Reset
        selectedViews.clear();
        selectedOriginalSizes.clear();
        groupStartPositions.clear();

        // currentlySelected reset
        if (currentlySelectedTextView != null && !mainLayout.equals(currentlySelectedTextView.getParent())) {
            currentlySelectedTextView = null;
        }
        if (currentlySelectedView != null && !mainLayout.equals(currentlySelectedView.getParent())) {
            currentlySelectedView = null;
        }

        seekMultiSize.setProgress(50);
        tvSizeLabel.setText("+0.0sp");
        updateMultiSelectBtnLabel();

        exportToJson();
        Toast.makeText(this, count + " elements delete થયા!", Toast.LENGTH_SHORT).show();
    }


    private void applyFrameColorMatrix(android.widget.ImageView iv,
                                       int brightness, int contrast, int saturation) {
        // brightness / contrast / saturation: 0–200 (100 = normal)
        float b = (brightness - 100) * 1.5f;   // −150 … +150
        float c = contrast / 100f;           //  0.0 … 2.0
        float tr = (-.5f * c + .5f) * 255f;
        float s = saturation / 100f;           //  0.0 … 2.0

        // Contrast matrix
        float[] cm = {
                c, 0, 0, 0, tr + b,
                0, c, 0, 0, tr + b,
                0, 0, c, 0, tr + b,
                0, 0, 0, 1, 0
        };

        // Saturation (luminance weights)
        float rW = 0.213f, gW = 0.715f, bW = 0.072f;
        float[] sm = {
                rW + (1 - rW) * s, gW - gW * s, bW - bW * s, 0, 0,
                rW - rW * s, gW + (1 - gW) * s, bW - bW * s, 0, 0,
                rW - rW * s, gW - gW * s, bW + (1 - bW) * s, 0, 0,
                0, 0, 0, 1, 0
        };

        android.graphics.ColorMatrix colorMatrix = new android.graphics.ColorMatrix();
        colorMatrix.set(cm);

        android.graphics.ColorMatrix satMatrix = new android.graphics.ColorMatrix();
        satMatrix.set(sm);
        colorMatrix.postConcat(satMatrix);

        iv.setColorFilter(new android.graphics.ColorMatrixColorFilter(colorMatrix));
    }


    private void applyFrameTintColor(
            final android.widget.ImageView targetSticker,
            final String maskUrl,
            final String topUrl,
            final int newColor) {

        if (topUrl.isEmpty()) {
            android.widget.Toast.makeText(this,
                    "Frame top URL નથી", android.widget.Toast.LENGTH_SHORT).show();
            return;
        }

        android.widget.Toast.makeText(this, "Tint apply...",
                android.widget.Toast.LENGTH_SHORT).show();

        // userMasked bitmap from tag
        Object bmpTag = targetSticker.getTag(R.id.tv_size_label);
        android.graphics.Bitmap userMasked =
                bmpTag instanceof android.graphics.Bitmap
                        ? (android.graphics.Bitmap) bmpTag : null;

        com.bumptech.glide.Glide.with(this).asBitmap().load(topUrl)
                .into(new com.bumptech.glide.request.target.CustomTarget<android.graphics.Bitmap>() {
                    @Override
                    public void onResourceReady(
                            @androidx.annotation.NonNull android.graphics.Bitmap topBmp,
                            @androidx.annotation.Nullable
                            com.bumptech.glide.request.transition.Transition<
                                    ? super android.graphics.Bitmap> t) {

                        int w = topBmp.getWidth(), h = topBmp.getHeight();
                        android.graphics.Bitmap result =
                                android.graphics.Bitmap.createBitmap(
                                        w, h, android.graphics.Bitmap.Config.ARGB_8888);
                        android.graphics.Canvas canvas = new android.graphics.Canvas(result);
                        android.graphics.Paint paint = new android.graphics.Paint(
                                android.graphics.Paint.ANTI_ALIAS_FLAG
                                        | android.graphics.Paint.FILTER_BITMAP_FLAG);

                        // layer 1: userMasked photo
                        if (userMasked != null) {
                            android.graphics.Bitmap us =
                                    android.graphics.Bitmap.createScaledBitmap(userMasked, w, h, true);
                            canvas.drawBitmap(us, 0, 0, paint);
                            us.recycle();
                        }

                        // layer 2: top overlay (tinted or plain)
                        android.graphics.Bitmap ts =
                                android.graphics.Bitmap.createScaledBitmap(topBmp, w, h, true);
                        if (newColor != android.graphics.Color.TRANSPARENT && newColor != 0) {
                            android.graphics.Bitmap tinted = applyColorTint(ts, newColor);
                            canvas.drawBitmap(tinted, 0, 0, paint);
                            tinted.recycle();
                        } else {
                            canvas.drawBitmap(ts, 0, 0, paint);
                        }
                        ts.recycle();

                        runOnUiThread(() -> {
                            targetSticker.setImageBitmap(result);
                            targetSticker.setColorFilter(null); // clear any matrix filter
                            android.widget.Toast.makeText(MainActivity.this,
                                    "✅ Tint apply!", android.widget.Toast.LENGTH_SHORT).show();
                        });
                    }

                    @Override
                    public void onLoadCleared(
                            @androidx.annotation.Nullable android.graphics.drawable.Drawable p) {
                    }
                });
    }


    @android.annotation.SuppressLint("ClickableViewAccessibility")
    private void showFrameImageControlsPopup(final android.widget.ImageView targetSticker) {

        // ── dismiss existing
        try {
            if (frameImageControlsPopup != null && frameImageControlsPopup.isShowing())
                frameImageControlsPopup.dismiss();
        } catch (Exception ignored) {
            frameImageControlsPopup = null;
        }

        try {
            if (selectionControlsPopup != null && selectionControlsPopup.isShowing())
                selectionControlsPopup.dismiss();
        } catch (Exception ignored) {
            selectionControlsPopup = null;
        }

        try {
            if (currentStickerToolbarPopup != null && currentStickerToolbarPopup.isShowing())
                currentStickerToolbarPopup.dismiss();
        } catch (Exception ignored) {
            currentStickerToolbarPopup = null;
        }

        // ── read tags
        Object maskTag = targetSticker.getTag(R.id.btn_location);
        Object topTag = targetSticker.getTag(R.id.btn_add_sticker);
        Object colorTag = targetSticker.getTag(R.id.seek_multi_size);
        final String maskUrl = maskTag != null ? maskTag.toString() : "";
        final String topUrl = topTag != null ? topTag.toString() : "";
        final int savedTint = colorTag instanceof Integer ? (int) colorTag : android.graphics.Color.TRANSPARENT;

        // ── inflate
        android.view.LayoutInflater inflater = android.view.LayoutInflater.from(this);
        android.view.View root = inflater.inflate(R.layout.popup_frame_image_controls, null);
        if (root == null) {
            android.widget.Toast.makeText(this,
                    "Layout inflate failed", android.widget.Toast.LENGTH_SHORT).show();
            return;
        }
        int popupW = ViewGroup.LayoutParams.MATCH_PARENT;

        frameImageControlsPopup = new android.widget.PopupWindow(
                root, popupW,
                android.widget.LinearLayout.LayoutParams.WRAP_CONTENT, false);

// ✅ Popup null check
        if (frameImageControlsPopup == null) {
            return;
        }

        frameImageControlsPopup.setTouchable(true);
        frameImageControlsPopup.setOutsideTouchable(false);
        frameImageControlsPopup.setBackgroundDrawable(
                new android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT));

        // ════════════════════════════════════════
        // DRAG HANDLE
        // ════════════════════════════════════════
        android.view.View dragHandle = root.findViewById(R.id.drag_handle_frame);
        final int[] popupXY = {framePopupLastX, framePopupLastY};
        final float[] lastRaw = {0f, 0f};

        dragHandle.setOnTouchListener((v, ev) -> {
            switch (ev.getAction()) {
                case android.view.MotionEvent.ACTION_DOWN:
                    lastRaw[0] = ev.getRawX();
                    lastRaw[1] = ev.getRawY();
                    return true;
                case android.view.MotionEvent.ACTION_MOVE:
                    popupXY[0] += (int) (ev.getRawX() - lastRaw[0]);
                    popupXY[1] += (int) (ev.getRawY() - lastRaw[1]);
                    lastRaw[0] = ev.getRawX();
                    lastRaw[1] = ev.getRawY();
                    if (frameImageControlsPopup != null && frameImageControlsPopup.isShowing())
                        frameImageControlsPopup.update(popupXY[0], popupXY[1], -1, -1);
                    framePopupLastX = popupXY[0];
                    framePopupLastY = popupXY[1];
                    isFramePopupMoved = true;
                    return true;
            }
            return false;
        });

        // ── Quick Lock button (always visible)
        android.widget.TextView btnQuickLock = root.findViewById(R.id.btn_frame_quick_lock);
        android.widget.TextView btnQuickDelete = root.findViewById(R.id.btn_frame_quick_delete);

// ── Lock state set
        boolean isLockedNow = lockedViews.contains(targetSticker);
        if (btnQuickLock != null) {
            btnQuickLock.setText(isLockedNow ? "🔒 Locked" : "🔓 Lock");
            btnQuickLock.setBackgroundColor(isLockedNow
                    ? android.graphics.Color.parseColor("#FFCDD2")
                    : android.graphics.Color.parseColor("#E8F5E9"));
            btnQuickLock.setTextColor(isLockedNow
                    ? android.graphics.Color.parseColor("#B71C1C")
                    : android.graphics.Color.parseColor("#1B5E20"));

            btnQuickLock.setOnClickListener(v -> {
                frameImageControlsPopup.dismiss();
                toggleLock(targetSticker);
            });
        }

// ── Quick Delete
        if (btnQuickDelete != null) {
            btnQuickDelete.setOnClickListener(v ->
                    new android.app.AlertDialog.Builder(this)
                            .setTitle("Delete Frame")
                            .setMessage("Photo frame delete કરવો?")
                            .setPositiveButton("Delete", (d, w) -> {
                                frameImageControlsPopup.dismiss();
                                mainLayout.removeView(targetSticker);
                                currentlySelectedView = null;
                                dismissSelectionControls();
                                exportToJson();
                                android.widget.Toast.makeText(this,
                                        "🗑 Frame deleted!", android.widget.Toast.LENGTH_SHORT).show();
                            })
                            .setNegativeButton("Cancel", null).show());
        }


        // ════════════════════════════════════════
        // TABS + PANELS
        // ════════════════════════════════════════
        android.widget.TextView tabPhoto = root.findViewById(R.id.tab_frame_photo);
        android.widget.TextView tabColor = root.findViewById(R.id.tab_frame_color);
        android.widget.TextView tabAdjust = root.findViewById(R.id.tab_frame_adjust);
        android.widget.TextView tabPosition = root.findViewById(R.id.tab_frame_position);

        android.widget.LinearLayout panelPhoto = root.findViewById(R.id.panel_frame_photo);
        android.widget.LinearLayout panelColor = root.findViewById(R.id.panel_frame_color);
        android.widget.LinearLayout panelAdjust = root.findViewById(R.id.panel_frame_adjust);
        android.widget.LinearLayout panelPosition = root.findViewById(R.id.panel_frame_position);

        android.widget.TextView[] allTabs = {tabPhoto, tabColor, tabAdjust, tabPosition};
        android.widget.LinearLayout[] allPanels = {panelPhoto, panelColor, panelAdjust, panelPosition};

        // ════════════════════════════════════════
// PHOTO TAB — SIZE SEEKBAR
// ════════════════════════════════════════
        android.widget.SeekBar seekPhotoSz = root.findViewById(R.id.seek_photo_tab_size);
        android.widget.TextView btnPhotoSzM = root.findViewById(R.id.btn_photo_tab_size_minus);
        android.widget.TextView btnPhotoSzP = root.findViewById(R.id.btn_photo_tab_size_plus);
        android.widget.TextView tvPhotoSzLbl = root.findViewById(R.id.tv_photo_tab_size_label);

// ── Original size save
        android.view.ViewGroup.LayoutParams _photoLp = targetSticker.getLayoutParams();
        final int[] photoOrigW = {(_photoLp != null && _photoLp.width > 0)
                ? _photoLp.width : targetSticker.getWidth()};
        final int[] photoOrigH = {(_photoLp != null && _photoLp.height > 0)
                ? _photoLp.height : targetSticker.getHeight()};

        if (tvPhotoSzLbl != null)
            tvPhotoSzLbl.setText("W:" + photoOrigW[0] + " H:" + photoOrigH[0]);

// ── Seekbar helper lambda
        Runnable applyPhotoSize = () -> {
            if (seekPhotoSz == null) return;
            int p = seekPhotoSz.getProgress();
            float delta = (p - 50) * 5f;
            android.view.ViewGroup.LayoutParams lp2 = targetSticker.getLayoutParams();
            lp2.width = (int) Math.max(60, photoOrigW[0] + delta);
            lp2.height = (int) Math.max(60, photoOrigH[0] + delta);
            targetSticker.setLayoutParams(lp2);
            if (tvPhotoSzLbl != null)
                tvPhotoSzLbl.setText("W:" + lp2.width + " H:" + lp2.height);
        };

        if (seekPhotoSz != null) {
            seekPhotoSz.setOnSeekBarChangeListener(
                    new android.widget.SeekBar.OnSeekBarChangeListener() {
                        @Override
                        public void onProgressChanged(
                                android.widget.SeekBar s, int p, boolean f) {
                            if (!f) return;
                            applyPhotoSize.run();
                        }

                        @Override
                        public void onStartTrackingTouch(
                                android.widget.SeekBar s) {
                        }

                        @Override
                        public void onStopTrackingTouch(
                                android.widget.SeekBar s) {
                            exportToJson();
                        }
                    });
        }

        if (btnPhotoSzM != null) {
            btnPhotoSzM.setOnClickListener(v -> {
                if (seekPhotoSz == null) return;
                int nv = Math.max(0, seekPhotoSz.getProgress() - 3);
                seekPhotoSz.setProgress(nv);
                applyPhotoSize.run();
                exportToJson();
            });
            btnPhotoSzM.setOnLongClickListener(v -> {
                if (seekPhotoSz == null) return true;
                int nv = Math.max(0, seekPhotoSz.getProgress() - 10);
                seekPhotoSz.setProgress(nv);
                applyPhotoSize.run();
                exportToJson();
                return true;
            });
        }

        if (btnPhotoSzP != null) {
            btnPhotoSzP.setOnClickListener(v -> {
                if (seekPhotoSz == null) return;
                int nv = Math.min(100, seekPhotoSz.getProgress() + 3);
                seekPhotoSz.setProgress(nv);
                applyPhotoSize.run();
                exportToJson();
            });
            btnPhotoSzP.setOnLongClickListener(v -> {
                if (seekPhotoSz == null) return true;
                int nv = Math.min(100, seekPhotoSz.getProgress() + 10);
                seekPhotoSz.setProgress(nv);
                applyPhotoSize.run();
                exportToJson();
                return true;
            });
        }

        android.view.View.OnClickListener tabClick = v -> {
            for (int i = 0; i < allTabs.length; i++) {
                boolean active = allTabs[i] == v;
                allTabs[i].setBackgroundColor(active
                        ? android.graphics.Color.parseColor("#1565C0")
                        : android.graphics.Color.parseColor("#BBDEFB"));
                allTabs[i].setTextColor(active
                        ? android.graphics.Color.WHITE
                        : android.graphics.Color.parseColor("#1565C0"));
                allPanels[i].setVisibility(active ? android.view.View.VISIBLE : android.view.View.GONE);
            }
        };
        tabPhoto.setOnClickListener(tabClick);
        tabColor.setOnClickListener(tabClick);
        tabAdjust.setOnClickListener(tabClick);
        tabPosition.setOnClickListener(tabClick);

        // ════════════════════════════════════════
        // PANEL 1 ─ PHOTO
        // ════════════════════════════════════════
        android.widget.TextView btnSelectPhoto = root.findViewById(R.id.btn_frame_select_photo);
        android.widget.TextView btnAdjustPhoto = root.findViewById(R.id.btn_frame_adjust_photo);
        android.widget.TextView btnReplacePhoto = root.findViewById(R.id.btn_frame_replace_photo);
        android.widget.TextView btnRemovePhoto = root.findViewById(R.id.btn_frame_remove_photo);

        // Select / Change photo
        btnSelectPhoto.setOnClickListener(v -> {
            frameImageControlsPopup.dismiss();
            currentFrameTargetSticker = targetSticker;
            android.content.Intent galleryIntent =
                    new android.content.Intent(android.content.Intent.ACTION_GET_CONTENT);
            galleryIntent.setType("image/*");
            startActivityForResult(
                    android.content.Intent.createChooser(galleryIntent, "Photo Select"),
                    REQUEST_FRAME_PHOTO_ADJUST);
        });

        // Zoom/Pan adjust of EXISTING photo
        btnAdjustPhoto.setOnClickListener(v -> {
            android.graphics.drawable.Drawable dr = targetSticker.getDrawable();
            android.graphics.Bitmap currentBmp = null;
            if (dr instanceof android.graphics.drawable.BitmapDrawable)
                currentBmp = ((android.graphics.drawable.BitmapDrawable) dr).getBitmap();

            if (currentBmp == null) {
                android.widget.Toast.makeText(this,
                        "પહેલા photo select કરો", android.widget.Toast.LENGTH_SHORT).show();
                return;
            }
            frameImageControlsPopup.dismiss();
            showFramePhotoAdjustDialog(currentBmp, null,
                    maskUrl, topUrl, savedTint, targetSticker);
        });

        // Frame Style Change (pick another frame)
        btnReplacePhoto.setOnClickListener(v -> {
            frameImageControlsPopup.dismiss();
            showFrameColorChangeDialog(targetSticker); // existing method reuse
        });

        // Remove photo — keep frame shell
        btnRemovePhoto.setOnClickListener(v ->
                new android.app.AlertDialog.Builder(this)
                        .setTitle("Photo Remove")
                        .setMessage("Frame ની photo remove કરવી? Frame design રહેશે.")
                        .setPositiveButton("Remove", (d, w) -> {
                            // placeholder bitmap
                            android.graphics.Bitmap ph = android.graphics.Bitmap.createBitmap(
                                    500, 500, android.graphics.Bitmap.Config.ARGB_8888);
                            android.graphics.Canvas phC = new android.graphics.Canvas(ph);
                            phC.drawColor(android.graphics.Color.parseColor("#CCCCCC"));
                            android.graphics.Paint pp = new android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG);
                            pp.setColor(android.graphics.Color.WHITE);
                            pp.setTextAlign(android.graphics.Paint.Align.CENTER);
                            pp.setTextSize(80);
                            phC.drawText("📷", 250, 290, pp);

                            // if topUrl available, re-draw frame on placeholder
                            if (!topUrl.isEmpty()) {
                                com.bumptech.glide.Glide.with(this).asBitmap().load(topUrl)
                                        .into(new com.bumptech.glide.request.target.CustomTarget<android.graphics.Bitmap>() {
                                            @Override
                                            public void onResourceReady(
                                                    @androidx.annotation.NonNull android.graphics.Bitmap topBmp,
                                                    @androidx.annotation.Nullable com.bumptech.glide.request.transition.Transition<? super android.graphics.Bitmap> t) {
                                                android.graphics.Bitmap res = android.graphics.Bitmap.createBitmap(
                                                        500, 500, android.graphics.Bitmap.Config.ARGB_8888);
                                                android.graphics.Canvas c = new android.graphics.Canvas(res);
                                                android.graphics.Paint p2 = new android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG | android.graphics.Paint.FILTER_BITMAP_FLAG);
                                                c.drawBitmap(ph, 0, 0, p2);
                                                android.graphics.Bitmap ts = android.graphics.Bitmap.createScaledBitmap(topBmp, 500, 500, true);
                                                c.drawBitmap(ts, 0, 0, p2);
                                                ts.recycle();
                                                runOnUiThread(() -> {
                                                    targetSticker.setImageBitmap(res);
                                                    targetSticker.setTag(R.id.tv_size_label, null);
                                                    exportToJson();
                                                    android.widget.Toast.makeText(MainActivity.this,
                                                            "✅ Photo removed!", android.widget.Toast.LENGTH_SHORT).show();
                                                });
                                            }

                                            @Override
                                            public void onLoadCleared(@androidx.annotation.Nullable android.graphics.drawable.Drawable p) {
                                            }
                                        });
                            } else {
                                targetSticker.setImageBitmap(ph);
                                targetSticker.setTag(R.id.tv_size_label, null);
                                exportToJson();
                                android.widget.Toast.makeText(this,
                                        "✅ Photo removed!", android.widget.Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("Cancel", null).show());

        // ════════════════════════════════════════
        // PANEL 2 ─ COLOR
        // ════════════════════════════════════════
        final int[] brt = {100}, con = {100}, sat = {100};
        final int[] tint = {savedTint};

        android.widget.TextView tvBrtLbl = root.findViewById(R.id.tv_brightness_label);
        android.widget.SeekBar seekBrt = root.findViewById(R.id.seek_brightness);
        android.widget.TextView tvConLbl = root.findViewById(R.id.tv_contrast_label);
        android.widget.SeekBar seekCon = root.findViewById(R.id.seek_contrast);
        android.widget.TextView tvSatLbl = root.findViewById(R.id.tv_saturation_label);
        android.widget.SeekBar seekSat = root.findViewById(R.id.seek_saturation);
        android.widget.TextView btnCustomTint = root.findViewById(R.id.btn_frame_tint_custom);
        android.widget.TextView btnRemoveTint = root.findViewById(R.id.btn_frame_remove_tint);
        android.widget.TextView btnColorReset = root.findViewById(R.id.btn_frame_color_reset);
        android.widget.LinearLayout rowTint = root.findViewById(R.id.row_tint_colors);

        // ── Quick tint color dots
        int[] tintColors = {
                android.graphics.Color.TRANSPARENT,
                android.graphics.Color.RED,
                android.graphics.Color.parseColor("#FF9800"),
                android.graphics.Color.YELLOW,
                android.graphics.Color.GREEN,
                android.graphics.Color.CYAN,
                android.graphics.Color.BLUE,
                android.graphics.Color.parseColor("#9C27B0"),
                android.graphics.Color.parseColor("#FF69B4"),
                android.graphics.Color.WHITE,
                android.graphics.Color.BLACK,
        };
        String[] tintNames = {"✕", "Red", "Org", "Ylw", "Grn", "Cyn", "Blu", "Pur", "Pnk", "Wht", "Blk"};

        for (int qi = 0; qi < tintColors.length; qi++) {
            final int qc = tintColors[qi];
            android.widget.TextView dot = new android.widget.TextView(this);
            dot.setText(tintNames[qi]);
            dot.setGravity(android.view.Gravity.CENTER);
            dot.setTextSize(8);
            android.graphics.drawable.GradientDrawable dotGd = new android.graphics.drawable.GradientDrawable();
            if (qc == android.graphics.Color.TRANSPARENT) {
                dotGd.setColor(android.graphics.Color.WHITE);
                dot.setTextColor(android.graphics.Color.RED);
            } else {
                dotGd.setColor(qc);
                dot.setTextColor(qc == android.graphics.Color.WHITE || qc == android.graphics.Color.YELLOW
                        ? android.graphics.Color.BLACK : android.graphics.Color.WHITE);
            }
            dotGd.setStroke(2, android.graphics.Color.GRAY);
            dotGd.setCornerRadius(6f);
            dot.setBackground(dotGd);
            android.widget.LinearLayout.LayoutParams dotLp =
                    new android.widget.LinearLayout.LayoutParams(
                            dpToPx(44), dpToPx(44));
            dotLp.setMargins(3, 0, 3, 0);
            dot.setLayoutParams(dotLp);
            dot.setOnClickListener(v -> {
                tint[0] = qc;
                applyFrameTintColor(targetSticker, maskUrl, topUrl, qc);
                targetSticker.setTag(R.id.seek_multi_size, qc);
            });
            rowTint.addView(dot);
        }

        // ── Seekbar listeners (reusable lambda)
        android.widget.SeekBar.OnSeekBarChangeListener colorListener =
                new android.widget.SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(android.widget.SeekBar s, int p, boolean f) {
                        if (!f) return;
                        if (s == seekBrt) {
                            brt[0] = p;
                            tvBrtLbl.setText("" + (p - 100));
                        }
                        if (s == seekCon) {
                            con[0] = p;
                            tvConLbl.setText("" + (p - 100));
                        }
                        if (s == seekSat) {
                            sat[0] = p;
                            tvSatLbl.setText("" + (p - 100));
                        }
                        applyFrameColorMatrix(targetSticker, brt[0], con[0], sat[0]);
                    }

                    @Override
                    public void onStartTrackingTouch(android.widget.SeekBar s) {
                    }

                    @Override
                    public void onStopTrackingTouch(android.widget.SeekBar s) {
                        exportToJson();
                    }
                };
        seekBrt.setOnSeekBarChangeListener(colorListener);
        seekCon.setOnSeekBarChangeListener(colorListener);
        seekSat.setOnSeekBarChangeListener(colorListener);

        btnCustomTint.setOnClickListener(v ->
                showColorPickerPopup(
                        tint[0] == android.graphics.Color.TRANSPARENT
                                ? android.graphics.Color.RED : tint[0],
                        color -> {
                            tint[0] = color;
                            applyFrameTintColor(targetSticker, maskUrl, topUrl, color);
                            targetSticker.setTag(R.id.seek_multi_size, color);
                            exportToJson();
                        }));

        btnRemoveTint.setOnClickListener(v -> {
            tint[0] = android.graphics.Color.TRANSPARENT;
            applyFrameTintColor(targetSticker, maskUrl, topUrl, android.graphics.Color.TRANSPARENT);
            targetSticker.setTag(R.id.seek_multi_size, android.graphics.Color.TRANSPARENT);
            exportToJson();
            android.widget.Toast.makeText(this, "Tint removed!", android.widget.Toast.LENGTH_SHORT).show();
        });

        btnColorReset.setOnClickListener(v -> {
            brt[0] = 100;
            con[0] = 100;
            sat[0] = 100;
            seekBrt.setProgress(100);
            seekCon.setProgress(100);
            seekSat.setProgress(100);
            tvBrtLbl.setText("0");
            tvConLbl.setText("0");
            tvSatLbl.setText("0");
            targetSticker.setColorFilter(null);
            exportToJson();
            android.widget.Toast.makeText(this, "✅ Reset!", android.widget.Toast.LENGTH_SHORT).show();
        });

        // ════════════════════════════════════════
        // PANEL 3 ─ ADJUST (Size / Rotation / Opacity / Flip)
        // ════════════════════════════════════════
        android.widget.TextView tvSzLbl = root.findViewById(R.id.tv_frame_size_label);
        android.widget.SeekBar seekSz = root.findViewById(R.id.seek_frame_size);
        android.widget.TextView btnSzM = root.findViewById(R.id.btn_frame_size_minus);
        android.widget.TextView btnSzP = root.findViewById(R.id.btn_frame_size_plus);

        android.widget.TextView tvRotLbl = root.findViewById(R.id.tv_frame_rotation_label);
        android.widget.SeekBar seekRot = root.findViewById(R.id.seek_frame_rotation);
        android.widget.TextView btnRotM = root.findViewById(R.id.btn_frame_rot_minus);
        android.widget.TextView btnRotP = root.findViewById(R.id.btn_frame_rot_plus);

        android.widget.TextView tvOpLbl = root.findViewById(R.id.tv_frame_opacity_label);
        android.widget.SeekBar seekOp = root.findViewById(R.id.seek_frame_opacity);

        android.widget.TextView btnFlipH = root.findViewById(R.id.btn_frame_flip_h);
        android.widget.TextView btnFlipV = root.findViewById(R.id.btn_frame_flip_v);

        // save original size
        android.view.ViewGroup.LayoutParams _lp = targetSticker.getLayoutParams();
        frameOrigW = (_lp != null && _lp.width > 0) ? _lp.width : targetSticker.getWidth();
        frameOrigH = (_lp != null && _lp.height > 0) ? _lp.height : targetSticker.getHeight();
        tvSzLbl.setText("W:" + frameOrigW + " H:" + frameOrigH);

        // Size seekbar
        seekSz.setOnSeekBarChangeListener(new android.widget.SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(android.widget.SeekBar s, int p, boolean f) {
                if (!f) return;
                float delta = (p - 50) * 5f;
                android.view.ViewGroup.LayoutParams lp2 = targetSticker.getLayoutParams();
                lp2.width = (int) Math.max(60, frameOrigW + delta);
                lp2.height = (int) Math.max(60, frameOrigH + delta);
                targetSticker.setLayoutParams(lp2);
                tvSzLbl.setText("W:" + lp2.width + " H:" + lp2.height);
            }

            @Override
            public void onStartTrackingTouch(android.widget.SeekBar s) {
            }

            @Override
            public void onStopTrackingTouch(android.widget.SeekBar s) {
                exportToJson();
            }
        });
        btnSzM.setOnClickListener(v -> {
            int nv = Math.max(0, seekSz.getProgress() - 3);
            seekSz.setProgress(nv);
            float delta = (nv - 50) * 5f;
            android.view.ViewGroup.LayoutParams lp2 = targetSticker.getLayoutParams();
            lp2.width = (int) Math.max(60, frameOrigW + delta);
            lp2.height = (int) Math.max(60, frameOrigH + delta);
            targetSticker.setLayoutParams(lp2);
            tvSzLbl.setText("W:" + lp2.width + " H:" + lp2.height);
            exportToJson();
        });
        btnSzP.setOnClickListener(v -> {
            int nv = Math.min(100, seekSz.getProgress() + 3);
            seekSz.setProgress(nv);
            float delta = (nv - 50) * 5f;
            android.view.ViewGroup.LayoutParams lp2 = targetSticker.getLayoutParams();
            lp2.width = (int) Math.max(60, frameOrigW + delta);
            lp2.height = (int) Math.max(60, frameOrigH + delta);
            targetSticker.setLayoutParams(lp2);
            tvSzLbl.setText("W:" + lp2.width + " H:" + lp2.height);
            exportToJson();
        });

        // Rotation
        seekRot.setProgress((int) (targetSticker.getRotation() + 180));
        seekRot.setOnSeekBarChangeListener(new android.widget.SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(android.widget.SeekBar s, int p, boolean f) {
                if (!f) return;
                float rot = p - 180f;
                targetSticker.setRotation(rot);
                tvRotLbl.setText((int) rot + "°");
            }

            @Override
            public void onStartTrackingTouch(android.widget.SeekBar s) {
            }

            @Override
            public void onStopTrackingTouch(android.widget.SeekBar s) {
                exportToJson();
            }
        });
        btnRotM.setOnClickListener(v -> {
            int nv = Math.max(0, seekRot.getProgress() - 5);
            seekRot.setProgress(nv);
            float rot = nv - 180f;
            targetSticker.setRotation(rot);
            tvRotLbl.setText((int) rot + "°");
            exportToJson();
        });
        btnRotP.setOnClickListener(v -> {
            int nv = Math.min(360, seekRot.getProgress() + 5);
            seekRot.setProgress(nv);
            float rot = nv - 180f;
            targetSticker.setRotation(rot);
            tvRotLbl.setText((int) rot + "°");
            exportToJson();
        });

        // Opacity
        seekOp.setProgress(Math.round(targetSticker.getAlpha() * 100f));
        seekOp.setOnSeekBarChangeListener(new android.widget.SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(android.widget.SeekBar s, int p, boolean f) {
                if (!f) return;
                targetSticker.setAlpha(p / 100f);
                tvOpLbl.setText(p + "%");
            }

            @Override
            public void onStartTrackingTouch(android.widget.SeekBar s) {
            }

            @Override
            public void onStopTrackingTouch(android.widget.SeekBar s) {
                exportToJson();
            }
        });

        // Flip
        btnFlipH.setOnClickListener(v -> {
            targetSticker.setScaleX(targetSticker.getScaleX() * -1);
            exportToJson();
        });
        btnFlipV.setOnClickListener(v -> {
            targetSticker.setScaleY(targetSticker.getScaleY() * -1);
            exportToJson();
        });

        // ════════════════════════════════════════
        // PANEL 4 ─ MOVE / POSITION
        // ════════════════════════════════════════
        android.widget.TextView btnMoveUp = root.findViewById(R.id.btn_frame_move_up);
        android.widget.TextView btnMoveDown = root.findViewById(R.id.btn_frame_move_down);
        android.widget.TextView btnMoveLeft = root.findViewById(R.id.btn_frame_move_left);
        android.widget.TextView btnMoveRight = root.findViewById(R.id.btn_frame_move_right);
        android.widget.TextView btnCenterH = root.findViewById(R.id.btn_frame_align_center_h);
        android.widget.TextView btnCenterV = root.findViewById(R.id.btn_frame_align_center_v);
        android.widget.TextView btnFront = root.findViewById(R.id.btn_frame_bring_front);
        android.widget.TextView btnLayerUp = root.findViewById(R.id.btn_frame_layer_up);
        android.widget.TextView btnLayerDown = root.findViewById(R.id.btn_frame_layer_down);
        android.widget.TextView btnSendBack = root.findViewById(R.id.btn_frame_send_back);
        android.widget.TextView btnLock = root.findViewById(R.id.btn_frame_lock);
        android.widget.TextView btnDelete = root.findViewById(R.id.btn_frame_delete);

        final int STEP = 10;
        btnMoveUp.setOnClickListener(v -> {
            targetSticker.setY(targetSticker.getY() - STEP);
            exportToJson();
        });
        btnMoveDown.setOnClickListener(v -> {
            targetSticker.setY(targetSticker.getY() + STEP);
            exportToJson();
        });
        btnMoveLeft.setOnClickListener(v -> {
            targetSticker.setX(targetSticker.getX() - STEP);
            exportToJson();
        });
        btnMoveRight.setOnClickListener(v -> {
            targetSticker.setX(targetSticker.getX() + STEP);
            exportToJson();
        });

        btnMoveUp.setOnLongClickListener(v -> {
            targetSticker.setY(targetSticker.getY() - STEP * 5);
            exportToJson();
            return true;
        });
        btnMoveDown.setOnLongClickListener(v -> {
            targetSticker.setY(targetSticker.getY() + STEP * 5);
            exportToJson();
            return true;
        });
        btnMoveLeft.setOnLongClickListener(v -> {
            targetSticker.setX(targetSticker.getX() - STEP * 5);
            exportToJson();
            return true;
        });
        btnMoveRight.setOnLongClickListener(v -> {
            targetSticker.setX(targetSticker.getX() + STEP * 5);
            exportToJson();
            return true;
        });

// Center H — main_image_view center use કરો:
        btnCenterH.setOnClickListener(v -> {
            float imgLeft = main_image_view.getLeft();
            float imgW = main_image_view.getWidth();
            targetSticker.setX(imgLeft + (imgW / 2f) - (targetSticker.getWidth() / 2f));
            exportToJson();
        });

// Center V:
        btnCenterV.setOnClickListener(v -> {
            float imgTop = main_image_view.getTop();
            float imgH = main_image_view.getHeight();
            targetSticker.setY(imgTop + (imgH / 2f) - (targetSticker.getHeight() / 2f));
            exportToJson();
        });

        // Layer
        btnFront.setOnClickListener(v -> {
            bringViewToFront(targetSticker);
            exportToJson();
            refreshLockedLayersPanel();
        });
        btnLayerUp.setOnClickListener(v -> {
            bringViewOneLayerUp(targetSticker);
            exportToJson();
            refreshLockedLayersPanel();
        });
        btnLayerDown.setOnClickListener(v -> {
            sendViewOneLayerDown(targetSticker);
            exportToJson();
            refreshLockedLayersPanel();
        });
        btnSendBack.setOnClickListener(v -> {
            sendViewToBack(targetSticker);
            exportToJson();
            refreshLockedLayersPanel();
        });

        // Lock
        boolean isLocked = lockedViews.contains(targetSticker);
        btnLock.setText(isLocked ? "🔒 Locked — Tap to Unlock" : "🔓 Lock");
        btnLock.setBackgroundColor(isLocked
                ? android.graphics.Color.parseColor("#FFCDD2")
                : android.graphics.Color.parseColor("#E8F5E9"));
        btnLock.setTextColor(isLocked
                ? android.graphics.Color.parseColor("#B71C1C")
                : android.graphics.Color.parseColor("#1B5E20"));
        btnLock.setOnClickListener(v -> {
            frameImageControlsPopup.dismiss();
            toggleLock(targetSticker);
        });

        // Delete
        btnDelete.setOnClickListener(v ->
                new android.app.AlertDialog.Builder(this)
                        .setTitle("Delete Frame")
                        .setMessage("Photo frame delete કરવો?")
                        .setPositiveButton("Delete", (d, w) -> {
                            frameImageControlsPopup.dismiss();
                            mainLayout.removeView(targetSticker);
                            currentlySelectedView = null;
                            dismissSelectionControls();
                            exportToJson();
                            android.widget.Toast.makeText(this,
                                    "🗑 Frame deleted!", android.widget.Toast.LENGTH_SHORT).show();
                        })
                        .setNegativeButton("Cancel", null).show());

        // ── Close button
        root.findViewById(R.id.btn_frame_popup_close)
                .setOnClickListener(v -> frameImageControlsPopup.dismiss());

        // ════════════════════════════════════════
        // SHOW POPUP
        // ════════════════════════════════════════
        if (isFramePopupMoved) {
            frameImageControlsPopup.showAtLocation(mainLayout,
                    android.view.Gravity.NO_GRAVITY,
                    framePopupLastX, framePopupLastY);
        } else {
            frameImageControlsPopup.showAtLocation(mainLayout,
                    android.view.Gravity.BOTTOM | android.view.Gravity.CENTER_HORIZONTAL,
                    0, 20);
            mainLayout.post(() -> {
                if (frameImageControlsPopup != null && frameImageControlsPopup.isShowing()) {
                    framePopupLastX = (mainLayout.getWidth() - popupW) / 2;
                    framePopupLastY = mainLayout.getHeight() - root.getHeight() - 20;
                }
            });
        }
    }


    private void setupMultiSelectToolbar() {

        View includeView = findViewById(R.id.include_multi_select_toolbar);
        if (includeView == null) return;

        // ── ✅ TextView cast
        seekMultiSize = includeView.findViewById(R.id.seek_multi_size);
        tvSizeLabel = includeView.findViewById(R.id.tv_size_label);
        seekMoveSpeed = includeView.findViewById(R.id.seek_move_speed);
        tvMoveSpeed = includeView.findViewById(R.id.tv_move_speed);
// ── Align buttons — include layout માં find કરો
        TextView btnAlignLeft = (TextView) includeView.findViewById(R.id.btn_ms_align_left);
        TextView btnAlignCenter = (TextView) includeView.findViewById(R.id.btn_ms_align_center);
        TextView btnAlignRight = (TextView) includeView.findViewById(R.id.btn_ms_align_right);
        TextView btnAlignTop = (TextView) includeView.findViewById(R.id.btn_ms_align_top);
        TextView btnAlignMiddle = (TextView) includeView.findViewById(R.id.btn_ms_align_middle);
        TextView btnAlignBottom = (TextView) includeView.findViewById(R.id.btn_ms_align_bottom);
        TextView btnDistributeH = (TextView) includeView.findViewById(R.id.btn_ms_distribute_h);
        TextView btnDistributeV = (TextView) includeView.findViewById(R.id.btn_ms_distribute_v);

        // ── Horizontal align
        btnAlignLeft.setOnClickListener(v -> alignSelectedViews("LEFT"));
        btnAlignCenter.setOnClickListener(v -> alignSelectedViews("CENTER_H"));
        btnAlignRight.setOnClickListener(v -> alignSelectedViews("RIGHT"));

// ── Vertical align
        btnAlignTop.setOnClickListener(v -> alignSelectedViews("TOP"));
        btnAlignMiddle.setOnClickListener(v -> alignSelectedViews("CENTER_V"));
        btnAlignBottom.setOnClickListener(v -> alignSelectedViews("BOTTOM"));

// ── Distribute evenly
        btnDistributeH.setOnClickListener(v -> alignSelectedViews("DISTRIBUTE_H"));
        btnDistributeV.setOnClickListener(v -> alignSelectedViews("DISTRIBUTE_V"));

        btnMsSelectAll = (TextView) includeView.findViewById(R.id.btn_ms_select_all);
        btnMsUnselect = (TextView) includeView.findViewById(R.id.btn_ms_unselect);
        btnMsCopy = (TextView) includeView.findViewById(R.id.btn_ms_copy);
        btnMsCancel = (TextView) includeView.findViewById(R.id.btn_ms_cancel);
        btnMsSizeMinus = (TextView) includeView.findViewById(R.id.btn_ms_size_minus);
        btnMsSizePlus = (TextView) includeView.findViewById(R.id.btn_ms_size_plus);
        btnMsDelete = (TextView) includeView.findViewById(R.id.btn_ms_delete);
        btnMsUp = (TextView) includeView.findViewById(R.id.btn_ms_up);
        btnMsDown = (TextView) includeView.findViewById(R.id.btn_ms_down);
        btnMsLeft = (TextView) includeView.findViewById(R.id.btn_ms_left);
        btnMsRight = (TextView) includeView.findViewById(R.id.btn_ms_right);
        btnSpeedSlow = (TextView) includeView.findViewById(R.id.btn_speed_slow);
        btnSpeedFast = (TextView) includeView.findViewById(R.id.btn_speed_fast);

        // ── Listeners same — setOnClickListener TextView પર work કરે
        btnMsCancel.setOnClickListener(v -> exitMultiSelectMode());

        btnMsDelete.setOnClickListener(v -> {
            if (selectedViews.isEmpty()) {
                Toast.makeText(this, "પહેલા elements select કરો", Toast.LENGTH_SHORT).show();
                return;
            }
            new android.app.AlertDialog.Builder(this).setTitle("Delete Confirmation").setMessage(selectedViews.size() + " elements delete કરવા છો?").setPositiveButton("Delete", (dialog, which) -> deleteSelectedViews()).setNegativeButton("Cancel", null).show();
        });

        btnMsSelectAll.setOnClickListener(v -> {
            selectedViews.clear();
            selectedOriginalSizes.clear();
            for (int i = 0; i < mainLayout.getChildCount(); i++) {
                View child = mainLayout.getChildAt(i);
                if (child == main_image_view) continue;
                if (lockedViews.contains(child)) continue;
                if (child instanceof TextView || child instanceof ImageView) {
                    selectedViews.add(child);
                    applySelectionBorder(child);
                    saveOriginalSize(child);
                }
            }
            seekMultiSize.setProgress(50);
            tvSizeLabel.setText("+0.0sp");
            updateMultiSelectBtnLabel();
        });

        btnMsUnselect.setOnClickListener(v -> {
            for (View sel : selectedViews) restoreViewBorder(sel);
            selectedViews.clear();
            selectedOriginalSizes.clear();
            seekMultiSize.setProgress(50);
            tvSizeLabel.setText("+0.0sp");
            updateMultiSelectBtnLabel();
        });

        btnMsCopy.setOnClickListener(v -> {
            if (selectedViews.isEmpty()) {
                Toast.makeText(this, "પહેલા elements select કરો", Toast.LENGTH_SHORT).show();
                return;
            }
            showCopyToPageDialog();
        });

        btnMsSizeMinus.setOnClickListener(v -> {
            int newVal = Math.max(0, seekMultiSize.getProgress() - 2);
            seekMultiSize.setProgress(newVal);
            applyMultiSizeChange(newVal);
        });
        btnMsSizeMinus.setOnLongClickListener(v -> {
            int newVal = Math.max(0, seekMultiSize.getProgress() - 5);
            seekMultiSize.setProgress(newVal);
            applyMultiSizeChange(newVal);
            return true;
        });

        btnMsSizePlus.setOnClickListener(v -> {
            int newVal = Math.min(100, seekMultiSize.getProgress() + 2);
            seekMultiSize.setProgress(newVal);
            applyMultiSizeChange(newVal);
        });
        btnMsSizePlus.setOnLongClickListener(v -> {
            int newVal = Math.min(100, seekMultiSize.getProgress() + 5);
            seekMultiSize.setProgress(newVal);
            applyMultiSizeChange(newVal);
            return true;
        });

        // ── Speed
        btnSpeedSlow.setOnClickListener(v -> {
            seekMoveSpeed.setProgress(0);
            currentMoveStep = SPEED_STEPS[0];
            tvMoveSpeed.setText(currentMoveStep + "px");
        });
        btnSpeedFast.setOnClickListener(v -> {
            seekMoveSpeed.setProgress(9);
            currentMoveStep = SPEED_STEPS[9];
            tvMoveSpeed.setText(currentMoveStep + "px");
        });

        // ── Arrow
        btnMsUp.setOnClickListener(v -> moveSelectedViews(0, -currentMoveStep));
        btnMsDown.setOnClickListener(v -> moveSelectedViews(0, currentMoveStep));
        btnMsLeft.setOnClickListener(v -> moveSelectedViews(-currentMoveStep, 0));
        btnMsRight.setOnClickListener(v -> moveSelectedViews(currentMoveStep, 0));

        btnMsUp.setOnLongClickListener(v -> {
            moveSelectedViews(0, -currentMoveStep * 3);
            return true;
        });
        btnMsDown.setOnLongClickListener(v -> {
            moveSelectedViews(0, currentMoveStep * 3);
            return true;
        });
        btnMsLeft.setOnLongClickListener(v -> {
            moveSelectedViews(-currentMoveStep * 3, 0);
            return true;
        });
        btnMsRight.setOnLongClickListener(v -> {
            moveSelectedViews(currentMoveStep * 3, 0);
            return true;
        });

        // ── SeekBar listeners
        seekMultiSize.setOnSeekBarChangeListener(new android.widget.SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(android.widget.SeekBar s, int progress, boolean fromUser) {
                if (!fromUser) return;
                applyMultiSizeChange(progress);
            }

            @Override
            public void onStartTrackingTouch(android.widget.SeekBar s) {
            }

            @Override
            public void onStopTrackingTouch(android.widget.SeekBar s) {
            }
        });

        seekMoveSpeed.setOnSeekBarChangeListener(new android.widget.SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(android.widget.SeekBar s, int progress, boolean fromUser) {
                currentMoveStep = SPEED_STEPS[progress];
                tvMoveSpeed.setText(currentMoveStep + "px");
            }

            @Override
            public void onStartTrackingTouch(android.widget.SeekBar s) {
            }

            @Override
            public void onStopTrackingTouch(android.widget.SeekBar s) {
            }
        });
    }


    private void alignSelectedViews(String mode) {
        if (selectedViews.isEmpty()) {
            Toast.makeText(this, "Elements select નથી", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedViews.size() < 2) {
            Toast.makeText(this, "ઓછામાં ઓછા 2 elements select કરો", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean allReady = true;
        for (View v : selectedViews) {
            if (v.getWidth() == 0 || v.getHeight() == 0) {
                allReady = false;
                break;
            }
        }

        if (!allReady) {
            // ✅ Layout pass પછી retry
            mainLayout.post(() -> alignSelectedViews(mode));
            return;
        }


        // ── Canvas bounds — getX()/getY() use કરો
        float canvasLeft = main_image_view.getX();
        float canvasTop = main_image_view.getY();
        float canvasRight = canvasLeft + main_image_view.getWidth();
        float canvasBottom = canvasTop + main_image_view.getHeight();
        float canvasCenterX = canvasLeft + main_image_view.getWidth() / 2f;
        float canvasCenterY = canvasTop + main_image_view.getHeight() / 2f;

        switch (mode) {

            case "LEFT":
                for (View v : selectedViews) {
                    v.setX(canvasLeft);
                }
                break;

            case "CENTER_H":
                for (View v : selectedViews) {
                    v.setX(canvasCenterX - v.getWidth() / 2f);
                }
                break;

            case "RIGHT":
                for (View v : selectedViews) {
                    v.setX(canvasRight - v.getWidth());
                }
                break;

            case "TOP":
                for (View v : selectedViews) {
                    v.setY(canvasTop);
                }
                break;

            case "CENTER_V":
                for (View v : selectedViews) {
                    v.setY(canvasCenterY - v.getHeight() / 2f);
                }
                break;

            case "BOTTOM":
                for (View v : selectedViews) {
                    v.setY(canvasBottom - v.getHeight());
                }
                break;

            case "DISTRIBUTE_H": {
                if (selectedViews.size() < 2) break;

                // ── X મુજબ sort
                List<View> sortedH = new ArrayList<>(selectedViews);
                sortedH.sort((a, b) -> Float.compare(a.getX(), b.getX()));

                // ── Total width of all views
                float totalW = 0;
                for (View v : sortedH)
                    totalW += v.getWidth();

                // ── Available space
                float available = main_image_view.getWidth() - totalW;
                float gap = available / (sortedH.size() - 1);

                float curX = canvasLeft;
                for (View v : sortedH) {
                    v.setX(curX);
                    curX += v.getWidth() + gap;
                }
                break;
            }

            case "DISTRIBUTE_V": {
                if (selectedViews.size() < 2) break;

                // ── Y મુજબ sort
                List<View> sortedV = new ArrayList<>(selectedViews);
                sortedV.sort((a, b) -> Float.compare(a.getY(), b.getY()));

                // ── Total height of all views
                float totalH = 0;
                for (View v : sortedV)
                    totalH += v.getHeight();

                // ── Available space
                float available = main_image_view.getHeight() - totalH;
                float gap = available / (sortedV.size() - 1);

                float curY = canvasTop;
                for (View v : sortedV) {
                    v.setY(curY);
                    curY += v.getHeight() + gap;
                }
                break;
            }
        }

        // ── GroupStartPositions update
        groupStartPositions.clear();
        for (View sel : selectedViews) {
            groupStartPositions.add(new float[]{sel.getX(), sel.getY()});
        }

        exportToJson();
        Toast.makeText(this, "✅ Align: " + mode, Toast.LENGTH_SHORT).show();
    }

    private void applyMultiSizeChange(int progress) {
        if (selectedOriginalSizes.isEmpty()) {
            tvSizeLabel.setText("Elements select કરો");
            Toast.makeText(this, "પહેલા elements select કરો", Toast.LENGTH_SHORT).show();
            seekMultiSize.setProgress(50); // reset
            return;
        }

        float deltaSp = (progress - 50) * 0.4f;
        tvSizeLabel.setText((deltaSp >= 0 ? "+" : "") + String.format("%.1f", deltaSp) + "sp");

        List<View> targets = new ArrayList<>(selectedViews);

        float density = getResources().getDisplayMetrics().scaledDensity;
        int sizeIdx = 0;

        for (int i = 0; i < targets.size(); i++) {
            View v = targets.get(i);

            if (v instanceof TextView) {
                if (sizeIdx < selectedOriginalSizes.size()) {
                    float origPx = selectedOriginalSizes.get(sizeIdx);
                    float origSp = origPx / density;
                    float newSp = Math.max(8f, Math.min(120f, origSp + deltaSp));
                    ((TextView) v).setTextSize(newSp);
                    sizeIdx++;
                }

            } else if (v instanceof ImageView && v != main_image_view) {
                if (sizeIdx + 1 < selectedOriginalSizes.size()) {
                    float dpDelta = (progress - 50) * 2f;
                    float origW = selectedOriginalSizes.get(sizeIdx);
                    float origH = selectedOriginalSizes.get(sizeIdx + 1);

                    android.view.ViewGroup.LayoutParams lp = v.getLayoutParams();
                    lp.width = (int) Math.max(50, origW + dpDelta);
                    lp.height = (int) Math.max(50, origH + dpDelta);
                    v.setLayoutParams(lp);
                    sizeIdx += 2;
                }
            }
        }
    }


    private void saveOriginalSize(View v) {
        if (v instanceof TextView) {
            // ── Current TEXT size px — seekbar move થાય તો UPDATE નહીં
            selectedOriginalSizes.add(((TextView) v).getTextSize());

        } else if (v instanceof ImageView && v != main_image_view) {
            android.view.ViewGroup.LayoutParams lp = v.getLayoutParams();
            float w = (lp != null && lp.width > 0) ? lp.width : v.getWidth();
            float h = (lp != null && lp.height > 0) ? lp.height : v.getHeight();
            selectedOriginalSizes.add(w);
            selectedOriginalSizes.add(h);
        }
    }

    // Multi-select mode EXIT
    private void exitMultiSelectMode() {
        for (View v : selectedViews) restoreViewBorder(v);
        selectedViews.clear();
        selectedOriginalSizes.clear();
        isMultiSelectMode = false;
        currentFilter = "ALL";
        // ✅ MultiSelect popup dismiss
        try {
            if (multiSelectPopup != null && multiSelectPopup.isShowing()) {
                multiSelectPopup.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        multiSelectPopup = null;

        // ✅ SelectionControls popup dismiss
        try {
            if (selectionControlsPopup != null && selectionControlsPopup.isShowing()) {
                selectionControlsPopup.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        selectionControlsPopup = null;

        // ✅ StickerToolbar popup dismiss
        try {
            if (currentStickerToolbarPopup != null && currentStickerToolbarPopup.isShowing()) {
                currentStickerToolbarPopup.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        currentStickerToolbarPopup = null;

        // ✅ Include view hide
        View includeView = findViewById(R.id.include_multi_select_toolbar);
        if (includeView != null) {
            includeView.setVisibility(View.GONE);
        }

        if (seekMultiSize != null) seekMultiSize.setProgress(50);
        if (tvSizeLabel != null) tvSizeLabel.setText("+0.0sp");

        groupStartPositions.clear();
        updateMultiSelectBtnLabel();
    }

    private void showCopyToPageDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Copy ક્યાં કરવું?");

        String[] options = new String[allPagesData.size() + 2];
        options[0] = "Same Page (Duplicate)";
        options[1] = "New Page બનાવીને copy";
        for (int i = 0; i < allPagesData.size(); i++) {
            options[i + 2] = "Page " + (i + 1) + (i == currentPageIndex ? " (Current)" : "");
        }

        builder.setItems(options, (dialog, which) -> {
            if (which == 0) {
                // ── Same page duplicate
                copyToSamePage();

            } else if (which == 1) {
                saveCurrentPage();

                // ── New empty page — no background
                JSONObject newPage = createEmptyPage();
                allPagesData.add(newPage);
                int newPageIndex = allPagesData.size() - 1;

                // ← currentImageUrl change નહીં — copyToPage navigate
                // વખતે target page ની imageUrl (empty) load થશે
                copyToPage(newPageIndex, true);

            } else {
                // ── Existing page
                int targetPage = which - 2;
                copyToPage(targetPage, targetPage != currentPageIndex);
            }
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    // Same page પર duplicate — copied views selected રહે
    private void copyToSamePage() {
        List<View> newlyCopied = new ArrayList<>();
        float sw = mainLayout.getWidth();
        float sh = mainLayout.getHeight();

        for (View v : selectedViews) {
            View copy = cloneView(v);
            if (copy == null) continue;

            mainLayout.addView(copy);

            // Offset 30px
            copy.post(() -> {
                copy.setX(v.getX() + 30);
                copy.setY(v.getY() + 30);
            });

            newlyCopied.add(copy);
        }

        // Deselect originals, select copies
        for (View v : selectedViews) restoreViewBorder(v);
        selectedViews.clear();

        for (View c : newlyCopied) {
            selectedViews.add(c);
            applySelectionBorder(c);
        }

        // Group move positions update
        groupStartPositions.clear();
        for (View sel : selectedViews) {
            groupStartPositions.add(new float[]{sel.getX(), sel.getY()});
        }

        updateMultiSelectBtnLabel();
        Toast.makeText(this, newlyCopied.size() + " elements copy થયા — selected રહ્યા", Toast.LENGTH_SHORT).show();
        exportToJson();
    }

    // બીજા page પર copy
    private void copyToPage(int targetPageIndex, boolean navigateToPage) {
        // Current page save
        saveCurrentPage();

        // Selected views ને JSON માં convert
        List<JSONObject> copiedTexts = new ArrayList<>();
        List<JSONObject> copiedStickers = new ArrayList<>();
        float sw = mainLayout.getWidth();
        float sh = mainLayout.getHeight();

        for (View v : selectedViews) {
            if (v instanceof StrokeTextView) {
                StrokeTextView tv = (StrokeTextView) v;
                JSONObject obj = new JSONObject();
                try {
                    obj.put("text", tv.getText().toString());
                    obj.put("xPercent", (tv.getX() / sw) * 100);
                    obj.put("yPercent", (tv.getY() / sh) * 100);
                    obj.put("color", tv.getCurrentTextColor());
                    obj.put("sizePercent", (tv.getTextSize() / sw) * 100);
                    obj.put("rotation", tv.getRotation());
                    obj.put("bgColor", getStoredBackgroundColor(tv));
                    obj.put("isBold", tv.getTypeface() != null && tv.getTypeface().isBold());
                    obj.put("isItalic", tv.getTypeface() != null && tv.getTypeface().isItalic());
                    obj.put("isUnderline", (tv.getPaintFlags() & Paint.UNDERLINE_TEXT_FLAG) != 0);
                    obj.put("isStrike", (tv.getPaintFlags() & Paint.STRIKE_THRU_TEXT_FLAG) != 0);
                    obj.put("letterSpace", tv.getLetterSpacing());
                    obj.put("lineSpace", tv.getLineSpacingMultiplier());
                    obj.put("strokeWidth", tv.getStrokeWidth());
                    obj.put("strokeColor", tv.getStrokeColor());
                    obj.put("borderStyle", tv.getTag(R.id.btn_add_sticker) != null ? (int) tv.getTag(R.id.btn_add_sticker) : 0);
                    obj.put("isArcMode", tv.isArcMode());
                    if (tv.isArcMode()) {
                        obj.put("arcAngle", tv.getArcAngle());
                        obj.put("arcRadius", tv.getRadius());
                        obj.put("arcUp", tv.isArcUp());
                    }
                    copiedTexts.add(obj);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else if (v instanceof ImageView && v != main_image_view) {


                ImageView iv = (ImageView) v;
                JSONObject sObj = new JSONObject();
                try {
                    Object uriTag = iv.getTag(R.id.btn_set_background);
                    sObj.put("uri", uriTag != null ? uriTag.toString() : "LOCATION_ICON");
                    Object locTag = iv.getTag(R.id.btn_location);
                    if (locTag != null) sObj.put("mapUrl", locTag.toString());
                    sObj.put("xPercent", (iv.getX() / sw) * 100);
                    sObj.put("yPercent", (iv.getY() / sh) * 100);
                    sObj.put("widthPercent", (iv.getWidth() * iv.getScaleX() / sw) * 100);
                    sObj.put("heightPercent", (iv.getHeight() * iv.getScaleY() / sh) * 100);
                    sObj.put("rotation", iv.getRotation());
                    copiedStickers.add(sObj);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        // ── Target page ના JSON માં ઉમેરો
        // ── imageUrl — target page ની જ રાખો, current page ની નહીં
        try {
            JSONObject targetPage = allPagesData.get(targetPageIndex);

            // ← imageUrl target page ની જ રહેશે — change નહીં
            // targetPage.put("imageUrl", ...) — આ line નથી

            JSONArray texts = targetPage.optJSONArray("texts");
            if (texts == null) texts = new JSONArray();
            for (JSONObject t : copiedTexts) texts.put(t);
            targetPage.put("texts", texts);

            JSONArray stickers = targetPage.optJSONArray("stickers");
            if (stickers == null) stickers = new JSONArray();
            for (JSONObject s : copiedStickers) stickers.put(s);
            targetPage.put("stickers", stickers);

            allPagesData.set(targetPageIndex, targetPage);

        } catch (Exception e) {
            e.printStackTrace();
        }

        // Multi-select reset
        for (View v : selectedViews) restoreViewBorder(v);
        selectedViews.clear();
        selectedOriginalSizes.clear();
        groupStartPositions.clear();
        isMultiSelectMode = false;

        View includeView = findViewById(R.id.include_multi_select_toolbar);
        if (includeView != null) {
            includeView.setVisibility(View.GONE);
        }

        updateMultiSelectBtnLabel(); //

        exportToJson();

        if (navigateToPage) {
            currentPageIndex = targetPageIndex;

            // ── Target page ની imageUrl — current નહીં
            JSONObject targetPage = allPagesData.get(targetPageIndex);
            currentImageUrl = targetPage.optString("imageUrl", "");

            loadPageData(allPagesData.get(currentPageIndex));
            updatePageIndicator();
            Toast.makeText(this, "Page " + (targetPageIndex + 1) + " પર copy + navigate!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Page " + (targetPageIndex + 1) + " પર copy થઈ ગયું!", Toast.LENGTH_SHORT).show();
        }
    }

    // View clone helper
    private View cloneView(View original) {
        if (original instanceof StrokeTextView) {
            StrokeTextView src = (StrokeTextView) original;
            StrokeTextView copy = new StrokeTextView(this);
            copy.setText(src.getText());
            copy.setTextColor(src.getCurrentTextColor());
            copy.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, src.getTextSize());
            copy.setPadding(20, 20, 20, 20);
            copy.setTypeface(src.getTypeface());
            copy.setPaintFlags(src.getPaintFlags());
            copy.setLetterSpacing(src.getLetterSpacing());
            copy.setRotation(src.getRotation());
            copy.setStrokeWidth(src.getStrokeWidth());
            copy.setStrokeColor(src.getStrokeColor());
            int bgColor = getStoredBackgroundColor(src);
            GradientDrawable gd = new GradientDrawable();
            gd.setColor(bgColor);
            gd.setStroke(4, Color.BLACK);
            gd.setCornerRadius(8f);
            copy.setBackground(gd);
            copy.setTag(bgColor);
            RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            copy.setLayoutParams(p);
            applyTouchListener(copy);
            return copy;

        } else if (original instanceof ImageView && original != main_image_view) {
            ImageView src = (ImageView) original;
            ImageView copy = new ImageView(this);
            copy.setImageDrawable(src.getDrawable());
            copy.setRotation(src.getRotation());
            copy.setScaleX(src.getScaleX());
            copy.setScaleY(src.getScaleY());
            Object uriTag = src.getTag(R.id.btn_set_background);
            if (uriTag != null) copy.setTag(R.id.btn_set_background, uriTag.toString());
            android.view.ViewGroup.LayoutParams op = src.getLayoutParams();
            RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(op.width, op.height);
            copy.setLayoutParams(p);
            applyTouchListenerForSticker(copy);
            return copy;
        }
        return null;
    }


    private void handleGroupMove(MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                // બધા selected views ની starting position save
                groupMoveStartX = event.getRawX();
                groupMoveStartY = event.getRawY();
                groupStartPositions.clear();
                for (View sel : selectedViews) {
                    groupStartPositions.add(new float[]{sel.getX(), sel.getY()});
                }
                isGroupMoving = true;
                break;

            case MotionEvent.ACTION_MOVE:
                if (!isGroupMoving) break;
                float dx = event.getRawX() - groupMoveStartX;
                float dy = event.getRawY() - groupMoveStartY;

                for (int i = 0; i < selectedViews.size(); i++) {
                    View sel = selectedViews.get(i);
                    if (i >= groupStartPositions.size()) break;
                    float[] startPos = groupStartPositions.get(i);

                    float newX = startPos[0] + dx;
                    float newY = startPos[1] + dy;

                    // Boundary
                    float minX = main_image_view.getLeft();
                    float maxX = main_image_view.getRight() - sel.getWidth();
                    float minY = main_image_view.getTop();
                    float maxY = main_image_view.getBottom() - sel.getHeight();

                    sel.setX(Math.max(minX, Math.min(newX, maxX)));
                    sel.setY(Math.max(minY, Math.min(newY, maxY)));
                }
                break;

            case MotionEvent.ACTION_UP:
                isGroupMoving = false;
                break;
        }
    }

    private void handleButtonClick(View id) {

        scrollToCenter(id);

        if (id.getId() == R.id.btn_add_text) { // જો તમે LinearLayout ને ID આપ્યું હોય તો

            addNewTextView();
        } else if (id.getId() == R.id.btn_set_background) {

            Dialog_Detail_Bottom dialog = Dialog_Detail_Bottom.newInstanceFromArrayList();

            dialog.setOnWallpaperSelectedListener(imageUrl -> {
                Log.e("BG_SET", "Background set: " + imageUrl);
                setBackgroundFromUrl(imageUrl);
            });

            dialog.show(getSupportFragmentManager(), "Dialog_Detail_Bottom");


        } else if (id.getId() == R.id.btn_save_json) {

            exportToJson();
        } else if (id.getId() == R.id.btn_location) {
            Intent intent = new Intent(this, LocationPickerActivity.class);
            startActivityForResult(intent, REQUEST_LOCATION_PICKER);
            //addLocationIconWithLink("https://maps.app.goo.gl/STa2S9Fqskw6MwaU6");
        } else if (id.getId() == R.id.btn_show_list) {

            exportToJson();
            android.content.Intent intent = new android.content.Intent(MainActivity.this, ListActivity.class);
            startActivity(intent);
        } else if (id.getId() == R.id.btn_add_sticker) {

            android.content.Intent intent = new android.content.Intent(android.content.Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(android.content.Intent.createChooser(intent, "Select Sticker"), PICK_STICKER_IMAGE);
        } else if (id.getId() == R.id.btn_add_page) {

            saveCurrentPage();
            allPagesData.add(createEmptyPage());
            currentPageIndex = allPagesData.size() - 1;
            loadPageData(allPagesData.get(currentPageIndex));
            updatePageIndicator();
        } else if (id.getId() == R.id.btn_delete_page) {

            if (allPagesData.size() > 1) {
                saveCurrentPage();

                deletedPagesList.add(allPagesData.get(currentPageIndex));
                allPagesData.remove(currentPageIndex);

                if (currentPageIndex >= allPagesData.size()) {
                    currentPageIndex = allPagesData.size() - 1;
                }

                loadPageData(allPagesData.get(currentPageIndex));
                updatePageIndicator();
                exportToJson();

                Toast.makeText(this, "Page moved to Deleted List", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "ઓછામાં ઓછું એક પેજ રાખવું જરૂરી છે!", Toast.LENGTH_SHORT).show();
            }
        } else if (id.getId() == R.id.btn_show_delete_page) {

            showDeletedPagesDialog();
        } else if (id.getId() == R.id.btn_show_deleted_texts) {

            showDeletedTextsDialog();
        } else if (id.getId() == R.id.btn_saveimage) {

            exportToJson(); // JSON ડેટા સેવ કરશે
            saveAllPagesAsImages(); // હવે આ બધા પેજને ફોટા તરીકે સેવ કરશે
        } else if (id.getId() == R.id.btn_download_pdf) {

            exportToJson(); // JSON સેવ કરો
            //saveAllPagesAsPdf(); // પીડીએફ ડાઉનલોડ શરૂ કરો
            saveAllPagesAsClickablePdf();
            Toast.makeText(this, "Pdf download thay che ", Toast.LENGTH_SHORT).show();
        } else if (id.getId() == R.id.btn_remove_bg_offline) {
            // ગેલેરી ખોલવા માટેનો ઇન્ટેન્ટ
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(Intent.createChooser(intent, "Select Image for Offline BG Removal"), 105);
        } else if (id.getId() == R.id.btn_arc_text) {
            // showArcTextPopup();
        } else if (id.getId() == R.id.btn_add_page) {
            saveCurrentPage(); // ← current page save (with current background)

            allPagesData.add(createEmptyPage());
            currentPageIndex = allPagesData.size() - 1;

            currentImageUrl = ""; // ← નવા page માટે reset

            loadPageData(allPagesData.get(currentPageIndex));
            updatePageIndicator();
        } else if (id.getId() == R.id.btn_sticker_gallery) {
            Intent intent = new Intent(this, invite_sticker_main_category.class);
            startActivityForResult(intent, REQUEST_STICKER_PICK);
        } else if (id.getId() == R.id.btn_photo_frame) {
            Intent intent = new Intent(this, FramePickerActivity.class);
            framePickerLauncher.launch(intent);

//            showPhotoFrameDialog();
        } else if (id.getId() == R.id.btn_pdf_list) {
            Intent intent = new Intent(this, PdfListActivity.class);
            startActivity(intent);
        } else if (id.getId() == R.id.btn_image_list) {
            Intent intent = new Intent(this, ImageListActivity.class);
            startActivity(intent);
        } else if (id.getId() == R.id.btn_grid_frame) {
            showGridFrameDialog();
        }
    }


    private void showGridFrameDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Grid Photo Frame");

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(24, 20, 24, 16);

        // ── Rows
        TextView lblRows = new TextView(this);
        lblRows.setText("Rows:");
        root.addView(lblRows);
        final int[] rows = {3};
        android.widget.NumberPicker npRows = new android.widget.NumberPicker(this);
        npRows.setMinValue(1);
        npRows.setMaxValue(10);
        npRows.setValue(3);
        npRows.setOnValueChangedListener((p, o, n) -> rows[0] = n);
        root.addView(npRows);

        // ── Cols
        TextView lblCols = new TextView(this);
        lblCols.setText("Columns:");
        root.addView(lblCols);
        final int[] cols = {4};
        android.widget.NumberPicker npCols = new android.widget.NumberPicker(this);
        npCols.setMinValue(1);
        npCols.setMaxValue(6);
        npCols.setValue(4);
        npCols.setOnValueChangedListener((p, o, n) -> cols[0] = n);
        root.addView(npCols);

        // ── Frame Shape
        TextView lblShape = new TextView(this);
        lblShape.setText("Photo Shape:");
        lblShape.setPadding(0, 12, 0, 4);
        root.addView(lblShape);

        final String[] shapes = {"CIRCLE", "ROUNDED", "SQUARE"};
        final String[] selectedShape = {"ROUNDED"};
        LinearLayout shapeRow = new LinearLayout(this);
        shapeRow.setOrientation(LinearLayout.HORIZONTAL);
        Button[] shapeBtns = new Button[3];
        String[] shapeLabels = {"Circle", "Rounded", "Square"};

        for (int i = 0; i < shapes.length; i++) {
            final int idx = i;
            Button sb = new Button(this);
            sb.setText(shapeLabels[i]);
            sb.setTextSize(12);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
            lp.setMargins(0, 0, i < 2 ? 6 : 0, 0);
            sb.setLayoutParams(lp);
            sb.setBackgroundColor(i == 1
                    ? Color.parseColor("#1565C0")
                    : Color.parseColor("#90CAF9"));
            sb.setTextColor(Color.WHITE);
            shapeBtns[i] = sb;
            sb.setOnClickListener(v -> {
                selectedShape[0] = shapes[idx];
                for (int j = 0; j < shapeBtns.length; j++)
                    shapeBtns[j].setBackgroundColor(
                            j == idx
                                    ? Color.parseColor("#1565C0")
                                    : Color.parseColor("#90CAF9"));
            });
            shapeRow.addView(sb);
        }
        root.addView(shapeRow);

        // ── Cell Size
        TextView lblSize = new TextView(this);
        lblSize.setText("Cell Size: 200px");
        lblSize.setPadding(0, 12, 0, 0);
        root.addView(lblSize);

        final int[] cellSize = {200};
        android.widget.SeekBar seekCell = new android.widget.SeekBar(this);
        seekCell.setMin(100);
        seekCell.setMax(400);
        seekCell.setProgress(200);
        seekCell.setOnSeekBarChangeListener(
                new android.widget.SeekBar.OnSeekBarChangeListener() {
                    public void onProgressChanged(android.widget.SeekBar s,
                                                  int p, boolean f) {
                        cellSize[0] = p;
                        lblSize.setText("Cell Size: " + p + "px");
                    }

                    public void onStartTrackingTouch(android.widget.SeekBar s) {
                    }

                    public void onStopTrackingTouch(android.widget.SeekBar s) {
                    }
                });
        root.addView(seekCell);

        // ── Name/Info toggle
        final boolean[] showName = {true};
        final boolean[] showInfo = {true};
        Button btnToggleName = new Button(this);
        btnToggleName.setText("✓ Name Text");
        btnToggleName.setBackgroundColor(Color.parseColor("#1565C0"));
        btnToggleName.setTextColor(Color.WHITE);
        btnToggleName.setOnClickListener(v -> {
            showName[0] = !showName[0];
            btnToggleName.setBackgroundColor(showName[0]
                    ? Color.parseColor("#1565C0")
                    : Color.parseColor("#90CAF9"));
            btnToggleName.setText(showName[0] ? "✓ Name Text" : "✕ Name Text");
        });
        root.addView(btnToggleName);

        Button btnToggleInfo = new Button(this);
        btnToggleInfo.setText("✓ Info Text (%)");
        btnToggleInfo.setBackgroundColor(Color.parseColor("#1565C0"));
        btnToggleInfo.setTextColor(Color.WHITE);
        btnToggleInfo.setOnClickListener(v -> {
            showInfo[0] = !showInfo[0];
            btnToggleInfo.setBackgroundColor(showInfo[0]
                    ? Color.parseColor("#1565C0")
                    : Color.parseColor("#90CAF9"));
            btnToggleInfo.setText(showInfo[0] ? "✓ Info Text (%)" : "✕ Info Text (%)");
        });
        root.addView(btnToggleInfo);

        builder.setView(new ScrollView(this) {{
            addView(root);
        }});
        builder.setPositiveButton("Create Grid", (d, w) ->
                createGridPhotoFrame(rows[0], cols[0],
                        selectedShape[0], cellSize[0],
                        showName[0], showInfo[0]));
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void createGridPhotoFrame(int rows, int cols,
                                      String shape, int cellSizePx,
                                      boolean showName, boolean showInfo) {

        int gap = 8;
        int nameH = showName ? 40 : 0;
        int infoH = showInfo ? 30 : 0;
        int cellTotalH = cellSizePx + nameH + infoH;

        int totalW = cols * cellSizePx + (cols - 1) * gap;
        int totalH = rows * cellTotalH + (rows - 1) * gap;

        // ── Outer RelativeLayout — mainLayout ના child
        RelativeLayout gridContainer = new RelativeLayout(this);

        RelativeLayout.LayoutParams outerLp =
                new RelativeLayout.LayoutParams(totalW, totalH);
        // ✅ CENTER — mainLayout center માં
        int cx = (mainLayout.getWidth() - totalW) / 2;
        int cy = (mainLayout.getHeight() - totalH) / 2;
        outerLp.leftMargin = cx > 0 ? cx : 0;
        outerLp.topMargin = cy > 0 ? cy : 0;
        gridContainer.setLayoutParams(outerLp);
        gridContainer.setTag(R.id.btn_set_background, "GRID_FRAME");

        List<JSONObject> cellDataList = new ArrayList<>();

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                final int cellIdx = r * cols + c;

                LinearLayout cell = new LinearLayout(this);
                cell.setOrientation(LinearLayout.VERTICAL);
                cell.setGravity(Gravity.CENTER_HORIZONTAL);

                RelativeLayout.LayoutParams cellLp =
                        new RelativeLayout.LayoutParams(
                                cellSizePx, cellTotalH);
                cellLp.leftMargin = c * (cellSizePx + gap);
                cellLp.topMargin = r * (cellTotalH + gap);
                cell.setLayoutParams(cellLp);

                // ── Photo ImageView
                ImageView photoIv = new ImageView(this);
                LinearLayout.LayoutParams photoLp =
                        new LinearLayout.LayoutParams(
                                cellSizePx, cellSizePx);
                photoIv.setLayoutParams(photoLp);
                photoIv.setScaleType(ImageView.ScaleType.CENTER_CROP);

                // ── Background placeholder
                GradientDrawable photoGd = new GradientDrawable();
                photoGd.setColor(Color.parseColor("#DDDDDD"));
                switch (shape) {
                    case "CIRCLE":
                        photoGd.setShape(GradientDrawable.OVAL);
                        break;
                    case "ROUNDED":
                        photoGd.setCornerRadius(cellSizePx * 0.12f);
                        break;
                }
                photoIv.setBackground(photoGd);

                // ── Clip outline
                photoIv.setClipToOutline(true);
                if ("CIRCLE".equals(shape)) {
                    photoIv.setOutlineProvider(
                            new android.view.ViewOutlineProvider() {
                                @Override
                                public void getOutline(View v,
                                                       android.graphics.Outline o) {
                                    o.setOval(0, 0,
                                            v.getWidth(), v.getHeight());
                                }
                            });
                } else if ("ROUNDED".equals(shape)) {
                    final float rad = cellSizePx * 0.12f;
                    photoIv.setOutlineProvider(
                            new android.view.ViewOutlineProvider() {
                                @Override
                                public void getOutline(View v,
                                                       android.graphics.Outline o) {
                                    o.setRoundRect(0, 0,
                                            v.getWidth(), v.getHeight(), rad);
                                }
                            });
                }

                photoIv.setTag(R.id.btn_set_background,
                        "GRID_CELL_" + cellIdx);

                // ── Click = photo select
                final ImageView finalPhoto = photoIv;

                setGridCellPhotoTouch(
                        finalPhoto,
                        gridContainer,
                        cellIdx,
                        cellDataList,
                        shape,
                        cellSizePx
                );

                cell.addView(photoIv);

                // ── Name TextView
                if (showName) {
                    TextView nameTv = new TextView(this);
                    nameTv.setText("Name " + (cellIdx + 1));
                    nameTv.setTextSize(13);
                    nameTv.setTextColor(Color.BLACK);
                    nameTv.setGravity(Gravity.CENTER);
                    nameTv.setTypeface(null, Typeface.BOLD);
                    nameTv.setMaxLines(1);
                    nameTv.setEllipsize(
                            android.text.TextUtils.TruncateAt.END);
                    LinearLayout.LayoutParams nameLp =
                            new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    nameH);
                    nameTv.setLayoutParams(nameLp);
                    nameTv.setTag(R.id.btn_ms_select_all,
                            "NAME_" + cellIdx);

                    // ── Name tap = edit
                    final TextView finalName = nameTv;
                    nameTv.setOnClickListener(v ->
                            showCellEditDialog(
                                    finalPhoto, finalName,
                                    null, cellIdx, cellDataList));
                    cell.addView(nameTv);
                }

                // ── Info TextView
                if (showInfo) {
                    TextView infoTv = new TextView(this);
                    infoTv.setText("0.00%");
                    infoTv.setTextSize(12);
                    infoTv.setTextColor(
                            Color.parseColor("#FF9800"));
                    infoTv.setGravity(Gravity.CENTER);
                    infoTv.setTypeface(null, Typeface.BOLD);
                    infoTv.setMaxLines(1);
                    LinearLayout.LayoutParams infoLp =
                            new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    infoH);
                    infoTv.setLayoutParams(infoLp);
                    infoTv.setTag(R.id.btn_ms_select_all,
                            "INFO_" + cellIdx);

                    final TextView finalInfo = infoTv;
                    infoTv.setOnClickListener(v ->
                            showCellEditDialog(
                                    finalPhoto, null,
                                    finalInfo, cellIdx, cellDataList));
                    cell.addView(infoTv);
                }

                // ── Cell JSON init
                try {
                    JSONObject obj = new JSONObject();
                    obj.put("photoUri", "");
                    obj.put("name", "Name " + (cellIdx + 1));
                    obj.put("info", "0.00%");
                    cellDataList.add(obj);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                gridContainer.addView(cell);
            }
        }

        gridContainer.setTag(R.id.btn_set_background, "GRID_FRAME");

        gridContainer.setTag(R.id.btn_grid_frame, new GridMeta(
                rows,
                cols,
                shape,
                cellSizePx,
                showName,
                showInfo,
                cellDataList
        ));

        applyTouchListenerForGrid(gridContainer);
        attachGridEditOpenListener(gridContainer);

        mainLayout.addView(gridContainer);


        // ✅ post — layout ready પછી center set
        mainLayout.post(() -> {
            int w = mainLayout.getWidth();
            int h = mainLayout.getHeight();
            gridContainer.setX((w - totalW) / 2f);
            gridContainer.setY((h - totalH) / 2f);
        });

        Toast.makeText(this,
                "✅ " + rows + "×" + cols +
                        " Grid add થઈ ગઈ! Cell tap = Photo/Edit",
                Toast.LENGTH_LONG).show();

        exportToJson();
    }


    private Bitmap clipBitmapToShape(Bitmap src,
                                     int size, String shape) {
        Bitmap result = Bitmap.createBitmap(
                size, size, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

        Bitmap scaled = Bitmap.createScaledBitmap(
                src, size, size, true);

        if ("CIRCLE".equals(shape)) {
            canvas.drawCircle(size / 2f, size / 2f,
                    size / 2f, paint);
            paint.setXfermode(new PorterDuffXfermode(
                    PorterDuff.Mode.SRC_IN));
            canvas.drawBitmap(scaled, 0, 0, paint);
        } else if ("ROUNDED".equals(shape)) {
            float r = size * 0.12f;
            canvas.drawRoundRect(0, 0, size, size, r, r, paint);
            paint.setXfermode(new PorterDuffXfermode(
                    PorterDuff.Mode.SRC_IN));
            canvas.drawBitmap(scaled, 0, 0, paint);
        } else {
            canvas.drawBitmap(scaled, 0, 0, paint);
        }
        paint.setXfermode(null);
        return result;
    }

    private void applyTouchListenerForGrid(RelativeLayout gridContainer) {

        final float[] downX = {0};
        final float[] downY = {0};
        final float[] startVX = {0};
        final float[] startVY = {0};

        gridContainer.setOnTouchListener((view, event) -> {
            switch (event.getActionMasked()) {

                case MotionEvent.ACTION_DOWN:
                    downX[0] = event.getRawX();
                    downY[0] = event.getRawY();
                    startVX[0] = view.getX();
                    startVY[0] = view.getY();

                    currentlySelectedGrid = (RelativeLayout) view;
                    selectView(view);
                    return true;

                case MotionEvent.ACTION_MOVE:
                    float dx = event.getRawX() - downX[0];
                    float dy = event.getRawY() - downY[0];

                    view.setX(startVX[0] + dx);
                    view.setY(startVY[0] + dy);

                    if (gridEditPopup != null && gridEditPopup.isShowing()) {
                        gridEditPopup.dismiss();
                    }
                    return true;

                case MotionEvent.ACTION_UP:
                    float dist = (float) Math.sqrt(
                            Math.pow(event.getRawX() - downX[0], 2) +
                                    Math.pow(event.getRawY() - downY[0], 2)
                    );

                    if (dist < 10) {
                        showGridEditPopup((RelativeLayout) view);
                    }

                    exportToJson();
                    return true;
            }
            return false;
        });
    }

    private void showGridCellFramePicker(RelativeLayout gridContainer) {

        Object tag = gridContainer.getTag(R.id.btn_grid_frame);
        if (!(tag instanceof GridMeta)) return;
        GridMeta meta = (GridMeta) tag;

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Grid Cell Frame Style");

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(16, 16, 16, 16);

        // ── Step label
        TextView lblStep = new TextView(this);
        lblStep.setText("Frame select કરો — grid cells પર apply થશે");
        lblStep.setTextSize(13);
        lblStep.setTextColor(Color.parseColor("#1565C0"));
        lblStep.setPadding(0, 0, 0, 8);
        root.addView(lblStep);

        // ── Selected frame preview
        final ImageView framePreview = new ImageView(this);
        framePreview.setScaleType(ImageView.ScaleType.FIT_CENTER);
        framePreview.setBackgroundColor(Color.parseColor("#F5F5F5"));
        LinearLayout.LayoutParams prevLp =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, 180);
        prevLp.setMargins(0, 0, 0, 8);
        framePreview.setLayoutParams(prevLp);
        root.addView(framePreview);

        // ── Remove frame button
        Button btnRemoveFrame = new Button(this);
        btnRemoveFrame.setText("✕ Frame Remove (Original Shape)");
        btnRemoveFrame.setTextColor(Color.WHITE);
        btnRemoveFrame.setBackgroundColor(Color.parseColor("#C62828"));
        LinearLayout.LayoutParams removeLp =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
        removeLp.setMargins(0, 0, 0, 8);
        btnRemoveFrame.setLayoutParams(removeLp);
        root.addView(btnRemoveFrame);

        // ── Frame RecyclerView
        ProgressBar progressBar = new ProgressBar(this);
        root.addView(progressBar);

        RecyclerView frameRV = new RecyclerView(this);
        LinearLayout.LayoutParams rvLp =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, 500);
        frameRV.setLayoutParams(rvLp);
        root.addView(frameRV);

        ScrollView sv = new ScrollView(this);
        sv.addView(root);

        final AlertDialog[] dialogRef = {null};
        dialogRef[0] = builder
                .setView(sv)
                .setNegativeButton("Cancel", null)
                .create();

        // ── Frame load
        loadFramesDirectly(frameRV, progressBar);

        // ── Frame click
        frameRV.addOnItemTouchListener(
                new invite_AppConstants.RecyclerTouchListener(
                        this, frameRV,
                        new invite_AppConstants.RecyclerTouchListener.ClickListener() {

                            @Override
                            public void onClick(View view, int position) {
                                if (invite_photo_frame.frame_arrayList == null
                                        || invite_photo_frame.frame_arrayList.isEmpty())
                                    return;

                                String frameUrl = invite_photo_frame.frame_arrayList
                                        .get(position).getImageBig();
                                String maskUrl = invite_photo_frame.frame_arrayList
                                        .get(position).getcard_background();
                                String topUrl = invite_photo_frame.frame_arrayList
                                        .get(position).getemail_icon();

                                Glide.with(MainActivity.this)
                                        .load(frameUrl)
                                        .into(framePreview);
                                lblStep.setText("✅ Frame selected! Apply button દબાવો");

                                currentGridFrameUrl = frameUrl;
                                currentGridFrameMaskUrl = maskUrl;
                                currentGridFrameTopUrl = topUrl;

                                // ── Apply button show
                                Button btnApply = new Button(MainActivity.this);
                                btnApply.setText("✅ All Cells પર Apply");
                                btnApply.setTextColor(Color.WHITE);
                                btnApply.setBackgroundColor(Color.parseColor("#1565C0"));
                                root.addView(btnApply, 2);

                                btnApply.setOnClickListener(av -> {
                                    if (dialogRef[0] != null
                                            && dialogRef[0].isShowing()) {
                                        dialogRef[0].dismiss();
                                    }
                                    applyFrameToGridCells(
                                            gridContainer, meta,
                                            maskUrl, topUrl,
                                            Color.TRANSPARENT);
                                });

                                Toast.makeText(MainActivity.this,
                                        "Frame select! Apply દબાવો",
                                        Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onLongClick(View view, int position) {
                            }
                        }));

        // ── Remove frame
        btnRemoveFrame.setOnClickListener(v -> {
            if (dialogRef[0] != null && dialogRef[0].isShowing()) {
                dialogRef[0].dismiss();
            }
            removeFrameFromGridCells(gridContainer, meta);
        });

        dialogRef[0].show();
    }


    private void showCellPhotoAdjustDialog(
            ImageView photoIv,
            Bitmap originalBitmap,
            String shape,
            int cellSizePx,
            int cellIdx,
            List<JSONObject> dataList,
            Uri photoUri) {

        android.app.Dialog dialog = new android.app.Dialog(
                this,
                android.R.style.Theme_Black_NoTitleBar_Fullscreen);

        RelativeLayout root = new RelativeLayout(this);
        root.setBackgroundColor(Color.parseColor("#CC000000"));
        dialog.setContentView(root);

        // ── Title
        TextView title = new TextView(this);
        title.setText("📷 Pinch Zoom + Drag to Adjust");
        title.setTextColor(Color.WHITE);
        title.setTextSize(13);
        title.setGravity(Gravity.CENTER);
        title.setBackgroundColor(Color.parseColor("#AA000000"));
        title.setPadding(16, 44, 16, 14);
        RelativeLayout.LayoutParams titleLp =
                new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.MATCH_PARENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT);
        titleLp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        root.addView(title, titleLp);

        // ── Preview size
        int previewSize = Math.min(
                getResources().getDisplayMetrics().widthPixels,
                getResources().getDisplayMetrics().heightPixels) - 120;

        // ── Frame tag check
        Object frameTag = photoIv.getTag(R.id.btn_sticker_gallery);
        String frameTagStr = frameTag != null
                ? frameTag.toString() : "";
        final boolean hasFrame = !frameTagStr.isEmpty()
                && frameTagStr.contains("|||");
        final String[] frameParts = hasFrame
                ? frameTagStr.split("\\|\\|\\|")
                : new String[]{"", ""};
        final String maskUrl =
                frameParts.length > 0 ? frameParts[0] : "";
        final String topUrl =
                frameParts.length > 1 ? frameParts[1] : "";

        // ── Matrix state
        final android.graphics.Matrix matrix =
                new android.graphics.Matrix();
        final android.graphics.Matrix savedMatrix =
                new android.graphics.Matrix();

        // ── Bitmap holders
        final Bitmap[] maskBitmapHolder = {null};
        final Bitmap[] topBitmapHolder = {null};

        // ✅ Custom Preview View
        final View previewView = new View(this) {
            @Override
            protected void onDraw(Canvas canvas) {
                super.onDraw(canvas);

                int w = getWidth();
                int h = getHeight();
                if (w <= 0 || h <= 0) return;

                // ── Dark background
                canvas.drawColor(Color.parseColor("#111111"));

                // ── Step 1: Photo bitmap from matrix
                Bitmap photoBmp = Bitmap.createBitmap(
                        w, h, Bitmap.Config.ARGB_8888);
                Canvas photoCanvas = new Canvas(photoBmp);
                Paint photoPaint = new Paint(
                        Paint.ANTI_ALIAS_FLAG
                                | Paint.FILTER_BITMAP_FLAG);
                photoCanvas.drawBitmap(
                        originalBitmap, matrix, photoPaint);

                // ── Step 2: Result bitmap — masked
                Bitmap resultBmp = Bitmap.createBitmap(
                        w, h, Bitmap.Config.ARGB_8888);
                Canvas resultCanvas = new Canvas(resultBmp);

                Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);

                if (hasFrame && maskBitmapHolder[0] != null) {

                    // ── Frame mask
                    Bitmap scaledMask = Bitmap.createScaledBitmap(
                            maskBitmapHolder[0], w, h, true);

                    boolean maskTransparent =
                            hasTransparentPixels(scaledMask);

                    if (maskTransparent) {
                        // Transparent hole = photo shows inside

                        // 1. Photo draw
                        resultCanvas.drawBitmap(photoBmp, 0, 0, p);

                        // 2. DST_IN — transparent = keep, opaque = remove
                        Paint dstIn = new Paint(Paint.ANTI_ALIAS_FLAG);
                        dstIn.setXfermode(new PorterDuffXfermode(
                                PorterDuff.Mode.DST_IN));
                        resultCanvas.drawBitmap(
                                scaledMask, 0, 0, dstIn);
                        dstIn.setXfermode(null);

                    } else {
                        // Opaque mask = shape stencil

                        // 1. Mask draw
                        resultCanvas.drawBitmap(scaledMask, 0, 0, p);

                        // 2. SRC_IN — only inside mask
                        Paint srcIn = new Paint(Paint.ANTI_ALIAS_FLAG);
                        srcIn.setXfermode(new PorterDuffXfermode(
                                PorterDuff.Mode.SRC_IN));
                        resultCanvas.drawBitmap(photoBmp, 0, 0, srcIn);
                        srcIn.setXfermode(null);
                    }

                    scaledMask.recycle();

                } else {

                    // ── No frame — shape clip
                    Paint shapePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
                    shapePaint.setColor(Color.WHITE);

                    if ("CIRCLE".equals(shape)) {
                        resultCanvas.drawCircle(
                                w / 2f, h / 2f,
                                Math.min(w, h) / 2f,
                                shapePaint);
                    } else if ("ROUNDED".equals(shape)) {
                        float r = w * 0.12f;
                        resultCanvas.drawRoundRect(
                                0, 0, w, h, r, r, shapePaint);
                    } else {
                        resultCanvas.drawRect(0, 0, w, h, shapePaint);
                    }

                    // Photo SRC_IN — only inside shape
                    Paint srcIn = new Paint(Paint.ANTI_ALIAS_FLAG);
                    srcIn.setXfermode(new PorterDuffXfermode(
                            PorterDuff.Mode.SRC_IN));
                    resultCanvas.drawBitmap(photoBmp, 0, 0, srcIn);
                    srcIn.setXfermode(null);
                }

                // ── Step 3: Result draw on main canvas
                canvas.drawBitmap(resultBmp, 0, 0, p);

                // ── Step 4: Top overlay
                if (hasFrame && topBitmapHolder[0] != null) {
                    Bitmap scaledTop = Bitmap.createScaledBitmap(
                            topBitmapHolder[0], w, h, true);
                    canvas.drawBitmap(scaledTop, 0, 0, p);
                    scaledTop.recycle();
                }

                // ── Step 5: Border line (no frame only)
                if (!hasFrame) {
                    Paint borderPaint =
                            new Paint(Paint.ANTI_ALIAS_FLAG);
                    borderPaint.setColor(Color.WHITE);
                    borderPaint.setStyle(Paint.Style.STROKE);
                    borderPaint.setStrokeWidth(3f);
                    borderPaint.setAlpha(180);

                    if ("CIRCLE".equals(shape)) {
                        canvas.drawCircle(
                                w / 2f, h / 2f,
                                Math.min(w, h) / 2f - 2,
                                borderPaint);
                    } else if ("ROUNDED".equals(shape)) {
                        float r = w * 0.12f;
                        canvas.drawRoundRect(
                                2, 2, w - 2, h - 2,
                                r, r, borderPaint);
                    } else {
                        canvas.drawRect(
                                2, 2, w - 2, h - 2, borderPaint);
                    }
                }

                // ── Cleanup
                photoBmp.recycle();
                resultBmp.recycle();
            }
        };

        previewView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        RelativeLayout.LayoutParams previewLp =
                new RelativeLayout.LayoutParams(
                        previewSize, previewSize);
        previewLp.addRule(RelativeLayout.CENTER_IN_PARENT);
        root.addView(previewView, previewLp);

        // ── Invisible touch view — gesture capture only
        final ImageView adjustIv = new ImageView(this);
        adjustIv.setImageBitmap(originalBitmap);
        adjustIv.setScaleType(ImageView.ScaleType.MATRIX);
        adjustIv.setAlpha(0f);
        root.addView(adjustIv, previewLp);

        // ── Initial fit
        adjustIv.post(() -> {
            float imgW = originalBitmap.getWidth();
            float imgH = originalBitmap.getHeight();
            float viewW = previewSize;
            float viewH = previewSize;
            float scale = Math.max(viewW / imgW, viewH / imgH);
            float dx = (viewW - imgW * scale) / 2f;
            float dy = (viewH - imgH * scale) / 2f;
            matrix.setScale(scale, scale);
            matrix.postTranslate(dx, dy);
            adjustIv.setImageMatrix(matrix);
            savedMatrix.set(matrix);
            previewView.invalidate();
        });

        // ── Mask load
        if (hasFrame && !maskUrl.isEmpty()) {
            Glide.with(this).asBitmap().load(maskUrl)
                    .into(new CustomTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(
                                @NonNull Bitmap bmp,
                                @Nullable Transition<
                                        ? super Bitmap> t) {
                            maskBitmapHolder[0] = bmp;
                            previewView.invalidate();
                        }

                        @Override
                        public void onLoadCleared(
                                @Nullable Drawable p) {
                        }
                    });
        }

        // ── Top overlay load
        if (hasFrame && !topUrl.isEmpty()) {
            Glide.with(this).asBitmap().load(topUrl)
                    .into(new CustomTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(
                                @NonNull Bitmap bmp,
                                @Nullable Transition<
                                        ? super Bitmap> t) {
                            topBitmapHolder[0] = bmp;
                            previewView.invalidate();
                        }

                        @Override
                        public void onLoadCleared(
                                @Nullable Drawable p) {
                        }
                    });
        }

        // ── Touch gesture
        final float[] lastX = {0};
        final float[] lastY = {0};
        final float[] midX = {0};
        final float[] midY = {0};
        final float[] startDist = {0};
        final int[] touchMode = {0};

        adjustIv.setOnTouchListener((v, event) -> {
            switch (event.getActionMasked()) {

                case MotionEvent.ACTION_DOWN:
                    savedMatrix.set(matrix);
                    lastX[0] = event.getX();
                    lastY[0] = event.getY();
                    touchMode[0] = 1;
                    break;

                case MotionEvent.ACTION_POINTER_DOWN:
                    if (event.getPointerCount() >= 2) {
                        startDist[0] = fingerSpacing(event);
                        if (startDist[0] > 10f) {
                            savedMatrix.set(matrix);
                            fingerMidPoint(midX, midY, event);
                            touchMode[0] = 2;
                        }
                    }
                    break;

                case MotionEvent.ACTION_MOVE:
                    if (touchMode[0] == 1) {
                        float dx = event.getX() - lastX[0];
                        float dy = event.getY() - lastY[0];
                        matrix.set(savedMatrix);
                        matrix.postTranslate(dx, dy);
                        adjustIv.setImageMatrix(matrix);
                        // ✅ Live update
                        previewView.invalidate();

                    } else if (touchMode[0] == 2
                            && event.getPointerCount() >= 2) {
                        float newDist = fingerSpacing(event);
                        if (newDist > 10f) {
                            float sf = newDist / startDist[0];
                            matrix.set(savedMatrix);
                            matrix.postScale(
                                    sf, sf, midX[0], midY[0]);
                            adjustIv.setImageMatrix(matrix);
                            // ✅ Live update
                            previewView.invalidate();
                        }
                    }
                    break;

                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_POINTER_UP:
                    touchMode[0] = 0;
                    break;
            }
            return true;
        });

        // ── Bottom buttons
        LinearLayout btnRow = new LinearLayout(this);
        btnRow.setOrientation(LinearLayout.HORIZONTAL);
        btnRow.setGravity(Gravity.CENTER);
        btnRow.setPadding(8, 14, 8, 36);
        btnRow.setBackgroundColor(Color.parseColor("#AA000000"));

        RelativeLayout.LayoutParams btnRowLp =
                new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.MATCH_PARENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT);
        btnRowLp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        root.addView(btnRow, btnRowLp);

        // ── Reset button
        Button btnReset = makeDlgBtn("↺ Reset",
                Color.parseColor("#546E7A"));
        btnReset.setOnClickListener(v -> {
            float imgW = originalBitmap.getWidth();
            float imgH = originalBitmap.getHeight();
            float sc = Math.max(
                    (float) previewSize / imgW,
                    (float) previewSize / imgH);
            float dx = (previewSize - imgW * sc) / 2f;
            float dy = (previewSize - imgH * sc) / 2f;
            matrix.setScale(sc, sc);
            matrix.postTranslate(dx, dy);
            adjustIv.setImageMatrix(matrix);
            savedMatrix.set(matrix);
            previewView.invalidate();
        });
        btnRow.addView(btnReset, makeBtnLp());

        // ── Cancel button
        Button btnCancel = makeDlgBtn("✕ Cancel",
                Color.parseColor("#C62828"));
        btnCancel.setOnClickListener(v -> dialog.dismiss());
        btnRow.addView(btnCancel, makeBtnLp());

        // ── Apply button
        Button btnApply = makeDlgBtn("✅ Apply",
                Color.parseColor("#1565C0"));
        btnApply.setOnClickListener(v -> {
            dialog.dismiss();
            applyCellPhotoFromMatrix(
                    photoIv, originalBitmap,
                    matrix, adjustIv,
                    shape, cellSizePx,
                    cellIdx, dataList,
                    photoUri,
                    hasFrame, maskUrl, topUrl);
        });
        btnRow.addView(btnApply, makeBtnLp());

        dialog.show();
    }


    // ── Matrix apply — crop + shape + frame
    private void applyCellPhotoFromMatrix(
            ImageView photoIv,
            Bitmap originalBitmap,
            android.graphics.Matrix matrix,
            ImageView adjustIv,
            String shape,
            int cellSizePx,
            int cellIdx,
            List<JSONObject> dataList,
            Uri photoUri,
            boolean hasFrame,
            String maskUrl,
            String topUrl) {

        // ── Invert matrix — view → image coordinates
        android.graphics.Matrix invertMatrix =
                new android.graphics.Matrix();
        matrix.invert(invertMatrix);

        int vW = adjustIv.getWidth();
        int vH = adjustIv.getHeight();

        // ── 4 corners transform
        float[] corners = {0, 0, vW, 0, vW, vH, 0, vH};
        invertMatrix.mapPoints(corners);

        float minX = Math.min(Math.min(corners[0], corners[2]),
                Math.min(corners[4], corners[6]));
        float minY = Math.min(Math.min(corners[1], corners[3]),
                Math.min(corners[5], corners[7]));
        float maxX = Math.max(Math.max(corners[0], corners[2]),
                Math.max(corners[4], corners[6]));
        float maxY = Math.max(Math.max(corners[1], corners[3]),
                Math.max(corners[5], corners[7]));

        int bW = originalBitmap.getWidth();
        int bH = originalBitmap.getHeight();
        minX = Math.max(0, minX);
        minY = Math.max(0, minY);
        maxX = Math.min(bW, maxX);
        maxY = Math.min(bH, maxY);

        int cropW = (int) (maxX - minX);
        int cropH = (int) (maxY - minY);

        if (cropW <= 0 || cropH <= 0) {
            // ── Fallback — whole bitmap
            cropW = bW;
            cropH = bH;
            minX = 0;
            minY = 0;
        }

        Bitmap cropped = Bitmap.createBitmap(
                originalBitmap,
                (int) minX, (int) minY, cropW, cropH);

        // ── Data save
        try {
            if (dataList != null && cellIdx >= 0
                    && cellIdx < dataList.size()) {
                if (photoUri != null) {
                    dataList.get(cellIdx).put(
                            "photoUri", photoUri.toString());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (hasFrame && !maskUrl.isEmpty()) {
            // ── Frame apply — mask + top overlay
            Glide.with(this)
                    .asBitmap()
                    .load(maskUrl)
                    .into(new com.bumptech.glide.request.target
                            .CustomTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(
                                @NonNull Bitmap maskBitmap,
                                @Nullable com.bumptech.glide.request.transition
                                        .Transition<? super Bitmap> t) {

                            applyFrameToCellWithPhoto(
                                    photoIv, cropped,
                                    maskBitmap, topUrl,
                                    Color.TRANSPARENT,
                                    cellSizePx);

                            // ── Also save shaped bitmap
                            Bitmap shaped = clipBitmapToShape(
                                    cropped, cellSizePx, shape);
                            try {
                                if (dataList != null && cellIdx >= 0
                                        && cellIdx < dataList.size()) {
                                    dataList.get(cellIdx).put(
                                            "photoBitmap",
                                            bitmapToBase64(shaped));
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            exportToJson();
                        }

                        @Override
                        public void onLoadCleared(
                                @Nullable Drawable p) {
                        }
                    });
        } else {
            // ── No frame — shape clip only
            Bitmap shaped = clipBitmapToShape(
                    cropped, cellSizePx, shape);
            cropped.recycle();

            photoIv.setImageBitmap(shaped);
            photoIv.setBackground(null);
            photoIv.setClipToOutline(false);

            try {
                if (dataList != null && cellIdx >= 0
                        && cellIdx < dataList.size()) {
                    dataList.get(cellIdx).put(
                            "photoBitmap",
                            bitmapToBase64(shaped));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            exportToJson();
        }

        Toast.makeText(this,
                "✅ Photo set!",
                Toast.LENGTH_SHORT).show();
    }


    // ── Helper: dialog button create
    private Button makeDlgBtn(String text, int bgColor) {
        Button b = new Button(this);
        b.setText(text);
        b.setTextColor(Color.WHITE);
        b.setTextSize(13);
        b.setBackgroundColor(bgColor);
        return b;
    }

    // ── Helper: button LayoutParams
    private LinearLayout.LayoutParams makeBtnLp() {
        LinearLayout.LayoutParams lp =
                new LinearLayout.LayoutParams(
                        0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        lp.setMargins(6, 0, 6, 0);
        return lp;
    }

    // ── Two finger spacing
    private float fingerSpacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    // ── Two finger midpoint
    private void fingerMidPoint(float[] midX, float[] midY,
                                MotionEvent event) {
        midX[0] = (event.getX(0) + event.getX(1)) / 2f;
        midY[0] = (event.getY(0) + event.getY(1)) / 2f;
    }


    // ── Two finger distance
    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    // ── Two finger midpoint
    private void midPoint(float[] midX, float[] midY,
                          MotionEvent event) {
        midX[0] = (event.getX(0) + event.getX(1)) / 2f;
        midY[0] = (event.getY(0) + event.getY(1)) / 2f;
    }


    private void applyFrameToGridCells(
            RelativeLayout gridContainer,
            GridMeta meta,
            String maskUrl,
            String topUrl,
            int overlayColor) {

        Toast.makeText(this,
                "Frame apply થઈ રહ્યો છે...",
                Toast.LENGTH_SHORT).show();

        // ── maskUrl = shape mask (transparent border)
        // ── topUrl  = decorative overlay (frame design)
        String loadUrl = !maskUrl.isEmpty() ? maskUrl : topUrl;

        Glide.with(this).asBitmap().load(loadUrl)
                .into(new com.bumptech.glide.request.target.CustomTarget<Bitmap>() {

                    @Override
                    public void onResourceReady(
                            @NonNull Bitmap maskBitmap,
                            @Nullable com.bumptech.glide.request.transition
                                    .Transition<? super Bitmap> t) {

                        // ── Frame tag save
                        gridContainer.setTag(
                                R.id.btn_sticker_gallery,
                                maskUrl + "|||" + topUrl);

                        for (int ci = 0;
                             ci < gridContainer.getChildCount(); ci++) {

                            View cellView = gridContainer.getChildAt(ci);
                            if (!(cellView instanceof LinearLayout)) continue;

                            LinearLayout cell = (LinearLayout) cellView;

                            for (int k = 0;
                                 k < cell.getChildCount(); k++) {

                                View child = cell.getChildAt(k);
                                Object bgTag =
                                        child.getTag(R.id.btn_set_background);

                                if (!(child instanceof ImageView)
                                        || bgTag == null
                                        || !bgTag.toString()
                                        .startsWith("GRID_CELL_"))
                                    continue;

                                ImageView photoIv = (ImageView) child;

                                // ── Frame tag store on cell
                                photoIv.setTag(
                                        R.id.btn_sticker_gallery,
                                        maskUrl + "|||" + topUrl);

                                // ── Photo bitmap get
                                Bitmap photoBmp = null;
                                android.graphics.drawable.Drawable dr =
                                        photoIv.getDrawable();
                                if (dr instanceof android.graphics.drawable
                                        .BitmapDrawable) {
                                    photoBmp = ((android.graphics.drawable
                                            .BitmapDrawable) dr).getBitmap();
                                }

                                if (photoBmp != null) {
                                    // ── Photo cell — frame apply
                                    applyFrameToCellWithPhoto(
                                            photoIv,
                                            photoBmp,
                                            maskBitmap,
                                            topUrl,
                                            overlayColor,
                                            meta.cellSizePx);
                                } else {
                                    // ── Empty cell
                                    applyEmptyCellFrame(
                                            photoIv,
                                            maskBitmap,
                                            topUrl,
                                            overlayColor,
                                            meta.cellSizePx);
                                }
                            }
                        }

                        exportToJson();
                        Toast.makeText(MainActivity.this,
                                "✅ Frame apply થઈ ગઈ!",
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable p) {
                    }

                    @Override
                    public void onLoadFailed(@Nullable Drawable e) {
                        Toast.makeText(MainActivity.this,
                                "Frame load failed — URL check કરો",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void applyFrameToCellWithPhoto(
            ImageView photoIv,
            Bitmap userPhoto,
            Bitmap maskBitmap,
            String topUrl,
            int overlayColor,
            int cellSizePx) {

        int w = maskBitmap.getWidth();
        int h = maskBitmap.getHeight();

        // ── Step 1: User photo scale
        Bitmap userScaled = Bitmap.createScaledBitmap(
                userPhoto, w, h, true);

        // ── Step 2: Mask apply — user photo ને mask shape માં clip
        Bitmap masked = Bitmap.createBitmap(
                w, h, Bitmap.Config.ARGB_8888);
        Canvas maskedCanvas = new Canvas(masked);

        Paint paint = new Paint(
                Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);

        // ✅ પહેલા user photo draw
        maskedCanvas.drawBitmap(userScaled, 0, 0, paint);

        // ✅ mask (DST_IN) — mask ના transparent area = photo hide
        Paint maskPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        maskPaint.setXfermode(
                new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        maskedCanvas.drawBitmap(maskBitmap, 0, 0, maskPaint);
        maskPaint.setXfermode(null);

        userScaled.recycle();

        if (topUrl == null || topUrl.isEmpty()) {
            // ── Top URL નથી — just masked photo set
            Bitmap cellSized = Bitmap.createScaledBitmap(
                    masked, cellSizePx, cellSizePx, true);
            runOnUiThread(() -> {
                photoIv.setImageBitmap(cellSized);
                photoIv.setBackground(null);
                photoIv.setClipToOutline(false);
            });
            masked.recycle();
            return;
        }

        // ── Step 3: Top overlay load + merge
        Glide.with(this)
                .asBitmap()
                .load(topUrl)
                .into(new com.bumptech.glide.request.target.CustomTarget<Bitmap>(w, h) {

                    @Override
                    public void onResourceReady(
                            @NonNull Bitmap topBitmap,
                            @Nullable com.bumptech.glide.request.transition
                                    .Transition<? super Bitmap> t) {

                        Bitmap result = Bitmap.createBitmap(
                                w, h, Bitmap.Config.ARGB_8888);
                        Canvas canvas = new Canvas(result);

                        Paint p = new Paint(
                                Paint.ANTI_ALIAS_FLAG
                                        | Paint.FILTER_BITMAP_FLAG);

                        // ✅ 1. Masked photo draw
                        canvas.drawBitmap(masked, 0, 0, p);

                        // ✅ 2. Top overlay draw (SRC_OVER)
                        Bitmap topScaled = Bitmap.createScaledBitmap(
                                topBitmap, w, h, true);

                        if (overlayColor != Color.TRANSPARENT
                                && overlayColor != 0) {
                            Bitmap tinted =
                                    applyColorTint(topScaled, overlayColor);
                            canvas.drawBitmap(tinted, 0, 0, p);
                            tinted.recycle();
                        } else {
                            canvas.drawBitmap(topScaled, 0, 0, p);
                        }
                        topScaled.recycle();

                        // ── Cell size scale
                        Bitmap cellSized = Bitmap.createScaledBitmap(
                                result, cellSizePx, cellSizePx, true);

                        runOnUiThread(() -> {
                            photoIv.setImageBitmap(cellSized);
                            photoIv.setBackground(null);
                            photoIv.setClipToOutline(false);
                            photoIv.setOutlineProvider(
                                    android.view.ViewOutlineProvider.BACKGROUND);
                        });

                        masked.recycle();
                        result.recycle();
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable p) {
                    }

                    @Override
                    public void onLoadFailed(@Nullable Drawable e) {
                        // Top fail — just masked photo
                        Bitmap cellSized = Bitmap.createScaledBitmap(
                                masked, cellSizePx, cellSizePx, true);
                        runOnUiThread(() -> {
                            photoIv.setImageBitmap(cellSized);
                            photoIv.setBackground(null);
                        });
                        masked.recycle();
                    }
                });
    }


    private void applyEmptyCellFrame(
            ImageView photoIv,
            Bitmap maskBitmap,
            String topUrl,
            int overlayColor,
            int cellSizePx) {

        int w = maskBitmap.getWidth();
        int h = maskBitmap.getHeight();

        Bitmap placeholder = Bitmap.createBitmap(
                w, h, Bitmap.Config.ARGB_8888);
        Canvas phCanvas = new Canvas(placeholder);
        phCanvas.drawColor(Color.parseColor("#CCCCCC"));

        Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(w * 0.1f);
        textPaint.setTextAlign(Paint.Align.CENTER);
        phCanvas.drawText("📷", w / 2f, h / 2f + 20, textPaint);

        // ✅ Fully qualified Transition
        Glide.with(this).asBitmap().load(topUrl)
                .into(new CustomTarget<Bitmap>(w, h) {

                    @Override
                    public void onResourceReady(@NonNull Bitmap topBitmap, @Nullable Transition<? super Bitmap> t) {

                        Bitmap result = Bitmap.createBitmap(
                                w, h, Bitmap.Config.ARGB_8888);
                        Canvas canvas = new Canvas(result);
                        Paint paint = new Paint(
                                Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);

                        canvas.drawBitmap(placeholder, 0, 0, paint);

                        Bitmap topScaled = Bitmap.createScaledBitmap(
                                topBitmap, w, h, true);

                        if (overlayColor != Color.TRANSPARENT) {
                            Bitmap tinted = applyColorTint(topScaled, overlayColor);
                            canvas.drawBitmap(tinted, 0, 0, paint);
                            tinted.recycle();
                        } else {
                            canvas.drawBitmap(topScaled, 0, 0, paint);
                        }
                        topScaled.recycle();

                        Bitmap cellSized = Bitmap.createScaledBitmap(
                                result, cellSizePx, cellSizePx, true);

                        runOnUiThread(() -> {
                            photoIv.setImageBitmap(cellSized);
                            photoIv.setBackground(null);
                            photoIv.setClipToOutline(false);
                        });

                        placeholder.recycle();
                        result.recycle();
                    }


                    @Override
                    public void onLoadCleared(@Nullable Drawable p) {
                    }
                });
    }


    private void removeFrameFromGridCells(
            RelativeLayout gridContainer,
            GridMeta meta) {

        // ── Frame tag clear
        gridContainer.setTag(R.id.btn_sticker_gallery, "");

        for (int ci = 0;
             ci < gridContainer.getChildCount(); ci++) {

            View cellView = gridContainer.getChildAt(ci);
            if (!(cellView instanceof LinearLayout)) continue;

            LinearLayout cell = (LinearLayout) cellView;

            for (int k = 0; k < cell.getChildCount(); k++) {
                View child = cell.getChildAt(k);
                Object bgTag = child.getTag(R.id.btn_set_background);

                if (!(child instanceof ImageView)
                        || bgTag == null
                        || !bgTag.toString().startsWith("GRID_CELL_"))
                    continue;

                ImageView photoIv = (ImageView) child;
                int cellIdx = ci;

                // ── Frame tag clear
                photoIv.setTag(R.id.btn_sticker_gallery, "");

                // ── Original shape restore
                photoIv.setClipToOutline(true);

                GradientDrawable gd = new GradientDrawable();
                gd.setColor(Color.parseColor("#DDDDDD"));

                if ("CIRCLE".equals(meta.shape)) {
                    gd.setShape(GradientDrawable.OVAL);
                    photoIv.setOutlineProvider(
                            new android.view.ViewOutlineProvider() {
                                @Override
                                public void getOutline(View v,
                                                       android.graphics.Outline o) {
                                    o.setOval(0, 0,
                                            v.getWidth(), v.getHeight());
                                }
                            });
                } else if ("ROUNDED".equals(meta.shape)) {
                    float rad = meta.cellSizePx * 0.12f;
                    gd.setCornerRadius(rad);
                    photoIv.setOutlineProvider(
                            new android.view.ViewOutlineProvider() {
                                @Override
                                public void getOutline(View v,
                                                       android.graphics.Outline o) {
                                    o.setRoundRect(0, 0,
                                            v.getWidth(), v.getHeight(),
                                            meta.cellSizePx * 0.12f);
                                }
                            });
                }

                // ── Restore original photo — URI load
                if (meta.cellDataList != null
                        && cellIdx < meta.cellDataList.size()) {
                    JSONObject cellData =
                            meta.cellDataList.get(cellIdx);

                    String b64 = cellData.optString("photoBitmap", "");
                    String uri = cellData.optString("photoUri", "");

                    if (!b64.isEmpty()) {
                        Bitmap bmp = base64ToBitmap(b64);
                        if (bmp != null) {
                            Bitmap clipped = clipBitmapToShape(
                                    bmp, meta.cellSizePx, meta.shape);
                            photoIv.setImageBitmap(clipped);
                            photoIv.setBackground(null);
                            continue;
                        }
                    }

                    if (!uri.isEmpty()) {
                        final ImageView finalIv = photoIv;
                        Glide.with(this).asBitmap().load(Uri.parse(uri))
                                .into(new CustomTarget<Bitmap>(
                                        meta.cellSizePx, meta.cellSizePx) {
                                    @Override
                                    public void onResourceReady(
                                            @NonNull Bitmap bmp,
                                            @Nullable Transition<
                                                    ? super Bitmap> t) {
                                        Bitmap clipped = clipBitmapToShape(
                                                bmp, meta.cellSizePx, meta.shape);
                                        finalIv.setImageBitmap(clipped);
                                        finalIv.setBackground(null);
                                    }

                                    @Override
                                    public void onLoadCleared(
                                            @Nullable Drawable p) {
                                    }
                                });
                        continue;
                    }
                }

                // ── Empty cell = placeholder
                photoIv.setImageDrawable(null);
                photoIv.setBackground(gd);
            }
        }

        exportToJson();
        Toast.makeText(this,
                "✅ Frame remove — original shape restore!",
                Toast.LENGTH_SHORT).show();
    }


    private void showAddCellDialog(
            RelativeLayout gridContainer,
            GridMeta meta,
            List<JSONObject> displayList,
            androidx.recyclerview.widget.RecyclerView.Adapter adapter,
            android.app.Dialog parentDialog) {

        AlertDialog.Builder builder =
                new AlertDialog.Builder(this);
        builder.setTitle("➕ New Cell Add");

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(24, 20, 24, 16);

        // ── Name input
        TextView lblName = new TextView(this);
        lblName.setText("Name:");
        lblName.setTextSize(13);
        lblName.setTextColor(Color.DKGRAY);
        root.addView(lblName);

        android.widget.EditText etName =
                new android.widget.EditText(this);
        etName.setHint("Name નાખો");
        etName.setTextSize(15);
        root.addView(etName);

        // ── Info input
        TextView lblInfo = new TextView(this);
        lblInfo.setText("Info (%):");
        lblInfo.setTextSize(13);
        lblInfo.setTextColor(Color.DKGRAY);
        lblInfo.setPadding(0, 12, 0, 4);
        root.addView(lblInfo);

        android.widget.EditText etInfo =
                new android.widget.EditText(this);
        etInfo.setHint("0.00%");
        etInfo.setTextSize(15);
        etInfo.setInputType(
                android.text.InputType.TYPE_CLASS_NUMBER
                        | android.text.InputType
                        .TYPE_NUMBER_FLAG_DECIMAL);
        root.addView(etInfo);

        // ── Quick % buttons
        LinearLayout quickRow = new LinearLayout(this);
        quickRow.setOrientation(LinearLayout.HORIZONTAL);
        quickRow.setPadding(0, 8, 0, 0);

        String[] quickVals = {"25%", "50%", "75%", "100%"};
        int[] quickColors = {
                Color.parseColor("#C62828"),
                Color.parseColor("#FF9800"),
                Color.parseColor("#1565C0"),
                Color.parseColor("#2E7D32")
        };

        for (int i = 0; i < quickVals.length; i++) {
            final String qv = quickVals[i];
            TextView qb = new TextView(this);
            qb.setText(qv);
            qb.setTextColor(Color.WHITE);
            qb.setTextSize(12);
            qb.setGravity(Gravity.CENTER);
            qb.setPadding(8, 8, 8, 8);

            GradientDrawable qGd = new GradientDrawable();
            qGd.setColor(quickColors[i]);
            qGd.setCornerRadius(10f);
            qb.setBackground(qGd);

            LinearLayout.LayoutParams qLp =
                    new LinearLayout.LayoutParams(
                            0,
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            1f);
            qLp.setMargins(3, 0, 3, 0);
            qb.setLayoutParams(qLp);

            qb.setOnClickListener(v -> {
                etInfo.setText(qv);
                etInfo.setSelection(
                        etInfo.getText().length());
            });
            quickRow.addView(qb);
        }
        root.addView(quickRow);

        // ── Position label
        TextView lblPos = new TextView(this);
        lblPos.setText("Position:");
        lblPos.setTextSize(13);
        lblPos.setTextColor(Color.DKGRAY);
        lblPos.setPadding(0, 12, 0, 4);
        root.addView(lblPos);

        // ── Position buttons
        LinearLayout posRow = new LinearLayout(this);
        posRow.setOrientation(LinearLayout.HORIZONTAL);

        final String[] selectedPos = {"END"};

        TextView btnPosStart = new TextView(this);
        btnPosStart.setText("⬆ Start");
        btnPosStart.setGravity(Gravity.CENTER);
        btnPosStart.setTextSize(12);
        btnPosStart.setTextColor(Color.WHITE);
        btnPosStart.setPadding(8, 8, 8, 8);

        TextView btnPosEnd = new TextView(this);
        btnPosEnd.setText("⬇ End");
        btnPosEnd.setGravity(Gravity.CENTER);
        btnPosEnd.setTextSize(12);
        btnPosEnd.setTextColor(Color.WHITE);
        btnPosEnd.setPadding(8, 8, 8, 8);

        TextView btnPosCustom = new TextView(this);
        btnPosCustom.setText("# Custom");
        btnPosCustom.setGravity(Gravity.CENTER);
        btnPosCustom.setTextSize(12);
        btnPosCustom.setTextColor(Color.WHITE);
        btnPosCustom.setPadding(8, 8, 8, 8);

        LinearLayout.LayoutParams posLp =
                new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1f);
        posLp.setMargins(3, 0, 3, 0);

        btnPosStart.setLayoutParams(posLp);
        btnPosEnd.setLayoutParams(
                new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1f));
        btnPosCustom.setLayoutParams(
                new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1f));

        // ── Custom position number picker
        android.widget.NumberPicker npPos =
                new android.widget.NumberPicker(this);
        npPos.setMinValue(1);
        npPos.setMaxValue(
                Math.max(1, meta.cellDataList.size() + 1));
        npPos.setValue(
                meta.cellDataList.size() + 1);
        npPos.setVisibility(View.GONE);

        // ── Update button styles
        Runnable updatePosUI = () -> {
            GradientDrawable activeGd =
                    new GradientDrawable();
            activeGd.setColor(
                    Color.parseColor("#1565C0"));
            activeGd.setCornerRadius(10f);

            GradientDrawable inactiveGd =
                    new GradientDrawable();
            inactiveGd.setColor(
                    Color.parseColor("#90CAF9"));
            inactiveGd.setCornerRadius(10f);

            btnPosStart.setBackground(
                    "START".equals(selectedPos[0])
                            ? activeGd : inactiveGd);
            btnPosEnd.setBackground(
                    "END".equals(selectedPos[0])
                            ? activeGd : inactiveGd);
            btnPosCustom.setBackground(
                    "CUSTOM".equals(selectedPos[0])
                            ? activeGd : inactiveGd);
            npPos.setVisibility(
                    "CUSTOM".equals(selectedPos[0])
                            ? View.VISIBLE : View.GONE);
        };

        // ── Default
        GradientDrawable activeGd =
                new GradientDrawable();
        activeGd.setColor(Color.parseColor("#1565C0"));
        activeGd.setCornerRadius(10f);
        btnPosEnd.setBackground(activeGd);

        GradientDrawable inactiveGd =
                new GradientDrawable();
        inactiveGd.setColor(
                Color.parseColor("#90CAF9"));
        inactiveGd.setCornerRadius(10f);
        btnPosStart.setBackground(inactiveGd);
        btnPosCustom.setBackground(inactiveGd);

        btnPosStart.setOnClickListener(v -> {
            selectedPos[0] = "START";
            updatePosUI.run();
        });

        btnPosEnd.setOnClickListener(v -> {
            selectedPos[0] = "END";
            updatePosUI.run();
        });

        btnPosCustom.setOnClickListener(v -> {
            selectedPos[0] = "CUSTOM";
            updatePosUI.run();
        });

        posRow.addView(btnPosStart);
        posRow.addView(btnPosEnd);
        posRow.addView(btnPosCustom);
        root.addView(posRow);
        root.addView(npPos);

        // ── Photo option
        TextView lblPhoto = new TextView(this);
        lblPhoto.setText("Photo (Optional):");
        lblPhoto.setTextSize(13);
        lblPhoto.setTextColor(Color.DKGRAY);
        lblPhoto.setPadding(0, 12, 0, 4);
        root.addView(lblPhoto);

        final String[] selectedPhotoUri = {""};

        ImageView photoPreview = new ImageView(this);
        photoPreview.setScaleType(
                ImageView.ScaleType.CENTER_CROP);
        photoPreview.setBackgroundColor(
                Color.parseColor("#EEEEEE"));

        GradientDrawable photoBg =
                new GradientDrawable();
        photoBg.setColor(Color.parseColor("#EEEEEE"));
        if ("CIRCLE".equals(meta.shape)) {
            photoBg.setShape(GradientDrawable.OVAL);
        } else if ("ROUNDED".equals(meta.shape)) {
            photoBg.setCornerRadius(
                    meta.cellSizePx * 0.12f);
        }
        photoPreview.setBackground(photoBg);

        LinearLayout.LayoutParams photoPreviewLp =
                new LinearLayout.LayoutParams(
                        dpToPx(80), dpToPx(80));
        photoPreviewLp.setMargins(0, 0, 0, 8);
        photoPreview.setLayoutParams(photoPreviewLp);
        root.addView(photoPreview);

        Button btnPickPhoto = new Button(this);
        btnPickPhoto.setText("📷 Photo Select");
        btnPickPhoto.setTextColor(Color.WHITE);
        btnPickPhoto.setBackgroundColor(
                Color.parseColor("#2E7D32"));
        root.addView(btnPickPhoto);

        // ── Temp store selected photo bitmap
        final Bitmap[] selectedPhotoBitmap = {null};
        final Uri[] selectedPhotoUriObj = {null};

        btnPickPhoto.setOnClickListener(v -> {
            // ── Store reference for result
            pendingGridCellDataList = meta.cellDataList;
            pendingGridShape = meta.shape;
            pendingGridCellSize = meta.cellSizePx;

            // ── Dismiss builder dialog temporarily
            // and reopen after photo
            // We use a flag approach instead
            Intent intent = new Intent(
                    Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(
                    Intent.createChooser(
                            intent, "Photo Select"),
                    REQUEST_GRID_CELL_PHOTO);
        });

        builder.setView(root);

        builder.setPositiveButton("➕ Add Cell",
                (d, w) -> {

                    String name = etName.getText()
                            .toString().trim();
                    String info = etInfo.getText()
                            .toString().trim();

                    // ── New cell JSON
                    JSONObject newCell = new JSONObject();
                    try {
                        newCell.put("name", name);
                        newCell.put("info", info);
                        newCell.put("photoUri",
                                selectedPhotoUri[0]);
                        newCell.put("photoBitmap", "");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    // ── Determine insert position
                    int insertPos;
                    if ("START".equals(selectedPos[0])) {
                        insertPos = 0;
                    } else if ("CUSTOM".equals(
                            selectedPos[0])) {
                        insertPos = Math.min(
                                npPos.getValue() - 1,
                                meta.cellDataList.size());
                    } else {
                        insertPos = meta.cellDataList.size();
                    }

                    // ── Insert into data list
                    meta.cellDataList.add(insertPos, newCell);

                    // ── Rebuild rows/cols
                    int total = meta.cellDataList.size();
                    int newCols = meta.cols;
                    int newRows = (int) Math.ceil(
                            (float) total / newCols);

                    // ── Rebuild grid
                    rebuildGridFrame(
                            gridContainer,
                            newRows, newCols,
                            meta.shape, meta.cellSizePx,
                            meta.showName, meta.showInfo,
                            meta.cellDataList);

                    exportToJson();

                    // ── Update list
                    displayList.clear();
                    displayList.addAll(meta.cellDataList);
                    adapter.notifyDataSetChanged();

                    Toast.makeText(this,
                            "✅ Cell " + (insertPos + 1) +
                                    " add થઈ ગઈ!",
                            Toast.LENGTH_SHORT).show();
                });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }


    private void showGridListDialog(RelativeLayout gridContainer) {

        Object tag = gridContainer.getTag(R.id.btn_grid_frame);
        if (!(tag instanceof GridMeta)) {
            Toast.makeText(this, "Grid data મળ્યો નહીં", Toast.LENGTH_SHORT).show();
            return;
        }
        GridMeta meta = (GridMeta) tag;

        if (meta.cellDataList == null || meta.cellDataList.isEmpty()) {
            Toast.makeText(this, "કોઈ data નથી", Toast.LENGTH_SHORT).show();
            return;
        }

        // ── Grid index find (page ઉપર કઈ grid છે)
        int gridIndex = 0;
        for (int i = 0; i < mainLayout.getChildCount(); i++) {
            View v = mainLayout.getChildAt(i);
            if (v == gridContainer) {
                gridIndex = i;
                break;
            }
        }

        // ── Cell data → JSONArray string
        org.json.JSONArray cellsArr = new org.json.JSONArray();
        for (JSONObject obj : meta.cellDataList) {
            cellsArr.put(obj);
        }

        // ── Intent build
        Intent intent = new Intent(this, GridListActivity.class);
        intent.putExtra(GridListActivity.EXTRA_CELLS_JSON, cellsArr.toString());
        intent.putExtra(GridListActivity.EXTRA_ROWS, meta.rows);
        intent.putExtra(GridListActivity.EXTRA_COLS, meta.cols);
        intent.putExtra(GridListActivity.EXTRA_SHAPE, meta.shape);
        intent.putExtra(GridListActivity.EXTRA_CELL_SIZE, meta.cellSizePx);
        intent.putExtra(GridListActivity.EXTRA_SHOW_NAME, meta.showName);
        intent.putExtra(GridListActivity.EXTRA_SHOW_INFO, meta.showInfo);
        intent.putExtra(GridListActivity.EXTRA_GRID_INDEX, gridIndex);

        // ── GridContainer reference store (result handle માટે)
        currentGridListTarget = gridContainer;

        startActivityForResult(intent, REQUEST_GRID_LIST_ACTIVITY);
    }


    // ── Inline Name Edit
    private void showInlineNameEdit(
            JSONObject obj,
            int cellIndex,
            GridMeta meta,
            RelativeLayout gridContainer,
            TextView tvName,
            android.app.Dialog parentDialog) {

        AlertDialog.Builder b =
                new AlertDialog.Builder(this);
        b.setTitle("✏ Cell " + (cellIndex + 1) +
                " — Name Edit");

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(24, 20, 24, 16);

        android.widget.EditText et =
                new android.widget.EditText(this);
        et.setText(obj.optString("name", ""));
        et.setSelection(et.getText().length());
        et.setHint("Name નાખો");
        et.setTextSize(16);
        root.addView(et);

        b.setView(root);
        b.setPositiveButton("Apply", (d, w) -> {
            String newName = et.getText()
                    .toString().trim();
            try {
                obj.put("name", newName);
            } catch (Exception e) {
                e.printStackTrace();
            }

            // ── Update grid UI
            TextView gridNameTv =
                    findGridCellTextView(
                            gridContainer,
                            cellIndex, "NAME_");
            if (gridNameTv != null) {
                gridNameTv.setText(newName);
            }

            // ── Update list UI
            tvName.setText(newName.isEmpty()
                    ? "(No Name)" : newName);

            exportToJson();
            Toast.makeText(this,
                    "✅ Name update!",
                    Toast.LENGTH_SHORT).show();
        });
        b.setNegativeButton("Cancel", null);
        b.show();
    }

    // ── Inline Info Edit
    private void showInlineInfoEdit(
            JSONObject obj,
            int cellIndex,
            GridMeta meta,
            RelativeLayout gridContainer,
            TextView tvInfo,
            android.app.Dialog parentDialog) {

        AlertDialog.Builder b =
                new AlertDialog.Builder(this);
        b.setTitle("% Cell " + (cellIndex + 1) +
                " — Info Edit");

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(24, 20, 24, 16);

        android.widget.EditText et =
                new android.widget.EditText(this);
        et.setText(obj.optString("info", ""));
        et.setSelection(et.getText().length());
        et.setHint("0.00%");
        et.setTextSize(16);
        et.setInputType(
                android.text.InputType.TYPE_CLASS_NUMBER
                        | android.text.InputType
                        .TYPE_NUMBER_FLAG_DECIMAL);
        root.addView(et);

        // ── Quick % buttons
        LinearLayout quickRow =
                new LinearLayout(this);
        quickRow.setOrientation(
                LinearLayout.HORIZONTAL);
        quickRow.setPadding(0, 12, 0, 0);

        String[] quickVals = {
                "25%", "50%", "75%", "100%"};
        int[] quickColors = {
                Color.parseColor("#C62828"),
                Color.parseColor("#FF9800"),
                Color.parseColor("#1565C0"),
                Color.parseColor("#2E7D32")
        };

        for (int i = 0; i < quickVals.length; i++) {
            final String qv = quickVals[i];
            TextView qb = new TextView(this);
            qb.setText(qv);
            qb.setTextColor(Color.WHITE);
            qb.setTextSize(12);
            qb.setGravity(Gravity.CENTER);
            qb.setPadding(8, 8, 8, 8);

            GradientDrawable qGd =
                    new GradientDrawable();
            qGd.setColor(quickColors[i]);
            qGd.setCornerRadius(10f);
            qb.setBackground(qGd);

            LinearLayout.LayoutParams qLp =
                    new LinearLayout.LayoutParams(
                            0,
                            LinearLayout.LayoutParams
                                    .WRAP_CONTENT, 1f);
            qLp.setMargins(3, 0, 3, 0);
            qb.setLayoutParams(qLp);

            qb.setOnClickListener(v -> {
                et.setText(qv);
                et.setSelection(
                        et.getText().length());
            });
            quickRow.addView(qb);
        }
        root.addView(quickRow);

        b.setView(root);
        b.setPositiveButton("Apply", (d, w) -> {
            String newInfo = et.getText()
                    .toString().trim();
            try {
                obj.put("info", newInfo);
            } catch (Exception e) {
                e.printStackTrace();
            }

            // ── Update grid UI
            TextView gridInfoTv =
                    findGridCellTextView(
                            gridContainer,
                            cellIndex, "INFO_");
            if (gridInfoTv != null) {
                gridInfoTv.setText(newInfo);
            }

            // ── Update list UI color
            tvInfo.setText(newInfo.isEmpty()
                    ? "—" : newInfo);
            try {
                float pct = Float.parseFloat(
                        newInfo.replace("%", "")
                                .replace(",", ".")
                                .trim());
                if (pct >= 75f)
                    tvInfo.setTextColor(
                            Color.parseColor("#2E7D32"));
                else if (pct >= 50f)
                    tvInfo.setTextColor(
                            Color.parseColor("#1565C0"));
                else if (pct >= 25f)
                    tvInfo.setTextColor(
                            Color.parseColor("#FF9800"));
                else
                    tvInfo.setTextColor(
                            Color.parseColor("#C62828"));
            } catch (Exception ignore) {
            }

            exportToJson();
            Toast.makeText(this,
                    "✅ Info update!",
                    Toast.LENGTH_SHORT).show();
        });
        b.setNegativeButton("Cancel", null);
        b.show();
    }

    // ── Mask Change Picker
// ── Mask Change Picker
    private void showCellMaskChangePicker(
            RelativeLayout gridContainer,
            int cellIndex,
            GridMeta meta) {

        AlertDialog.Builder builder =
                new AlertDialog.Builder(this);
        builder.setTitle("🖼 Cell " +
                (cellIndex + 1) + " — Mask Change");

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(16, 16, 16, 16);

        // ── Info label
        TextView lblInfo = new TextView(this);
        lblInfo.setText("Frame/Mask select કરો");
        lblInfo.setTextSize(13);
        lblInfo.setTextColor(Color.parseColor("#1565C0"));
        lblInfo.setPadding(0, 0, 0, 8);
        root.addView(lblInfo);

        // ── Selected frame preview
        ImageView framePreview = new ImageView(this);
        framePreview.setScaleType(ImageView.ScaleType.FIT_CENTER);
        framePreview.setBackgroundColor(Color.parseColor("#F5F5F5"));
        LinearLayout.LayoutParams prevLp =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        dpToPx(140));
        prevLp.setMargins(0, 0, 0, 8);
        framePreview.setLayoutParams(prevLp);
        root.addView(framePreview);

        // ── Remove frame button
        Button btnRemove = new Button(this);
        btnRemove.setText("✕ Frame Remove");
        btnRemove.setTextColor(Color.WHITE);
        btnRemove.setBackgroundColor(Color.parseColor("#C62828"));
        LinearLayout.LayoutParams removeLp =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
        removeLp.setMargins(0, 0, 0, 8);
        btnRemove.setLayoutParams(removeLp);
        root.addView(btnRemove);

        // ── Progress + RecyclerView
        ProgressBar progressBar = new ProgressBar(this);
        root.addView(progressBar);

        RecyclerView frameRV = new RecyclerView(this);
        LinearLayout.LayoutParams rvLp =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        dpToPx(400));
        frameRV.setLayoutParams(rvLp);
        root.addView(frameRV);

        final AlertDialog[] dialogRef = {null};

        ScrollView sv = new ScrollView(this);
        sv.addView(root);

        dialogRef[0] = builder
                .setView(sv)
                .setNegativeButton("Cancel", null)
                .create();

        // ── Load frames
        loadFramesDirectly(frameRV, progressBar);

        // ── Frame click
        frameRV.addOnItemTouchListener(
                new invite_AppConstants.RecyclerTouchListener(
                        this, frameRV,
                        new invite_AppConstants.RecyclerTouchListener.ClickListener() {

                            @Override
                            public void onClick(View view, int position) {

                                if (invite_photo_frame.frame_arrayList == null
                                        || invite_photo_frame.frame_arrayList.isEmpty())
                                    return;

                                String newMaskUrl = invite_photo_frame
                                        .frame_arrayList.get(position)
                                        .getcard_background();
                                String newTopUrl = invite_photo_frame
                                        .frame_arrayList.get(position)
                                        .getemail_icon();
                                String frameUrl = invite_photo_frame
                                        .frame_arrayList.get(position)
                                        .getImageBig();

                                // ── Preview
                                Glide.with(MainActivity.this)
                                        .load(frameUrl)
                                        .into(framePreview);

                                lblInfo.setText("✅ Frame selected! Apply દબાવો");

                                // ── Apply button
                                Button btnApply = new Button(MainActivity.this);
                                btnApply.setText("✅ Apply to Cell " + (cellIndex + 1));
                                btnApply.setTextColor(Color.WHITE);
                                btnApply.setBackgroundColor(
                                        Color.parseColor("#1565C0"));

                                // ── Remove old apply button if exists
                                if (root.getChildCount() > 0) {
                                    View existing = root.getChildAt(2);
                                    if (existing instanceof Button) {
                                        root.removeViewAt(2);
                                    }
                                }
                                root.addView(btnApply, 2);

                                btnApply.setOnClickListener(av -> {

                                    if (dialogRef[0] != null
                                            && dialogRef[0].isShowing()) {
                                        dialogRef[0].dismiss();
                                    }

                                    // ── Find cell photo IV
                                    ImageView cellPhotoIv =
                                            findGridCellPhotoIv(
                                                    gridContainer, cellIndex);

                                    if (cellPhotoIv == null) return;

                                    // ── Frame tag set
                                    cellPhotoIv.setTag(
                                            R.id.btn_sticker_gallery,
                                            newMaskUrl + "|||" + newTopUrl);

                                    // ── Current photo get
                                    Bitmap currentPhoto = null;
                                    android.graphics.drawable.Drawable dr =
                                            cellPhotoIv.getDrawable();
                                    if (dr instanceof
                                            android.graphics.drawable.BitmapDrawable) {
                                        currentPhoto =
                                                ((android.graphics.drawable.BitmapDrawable) dr)
                                                        .getBitmap();
                                    }

                                    if (currentPhoto != null) {

                                        final Bitmap finalPhoto = currentPhoto;

                                        Glide.with(MainActivity.this)
                                                .asBitmap()
                                                .load(newMaskUrl)
                                                .into(new CustomTarget<Bitmap>() {

                                                    @Override
                                                    public void onResourceReady(
                                                            @NonNull Bitmap mb,
                                                            @Nullable Transition<? super Bitmap> t) {

                                                        applyFrameToCellWithPhoto(
                                                                cellPhotoIv,
                                                                finalPhoto,
                                                                mb,
                                                                newTopUrl,
                                                                Color.TRANSPARENT,
                                                                meta.cellSizePx);

                                                        // ── DataList update
                                                        try {
                                                            List<JSONObject> dList = meta.cellDataList;
                                                            if (dList != null && cellIndex < dList.size()) {
                                                                dList.get(cellIndex).put("frameMask", newMaskUrl);
                                                                dList.get(cellIndex).put("frameTop", newTopUrl);
                                                            }
                                                        } catch (Exception e) {
                                                            e.printStackTrace();
                                                        }

                                                        exportToJson();
                                                        Toast.makeText(
                                                                MainActivity.this,
                                                                "✅ Mask apply!",
                                                                Toast.LENGTH_SHORT).show();
                                                    }

                                                    @Override
                                                    public void onLoadCleared(
                                                            @Nullable Drawable p) {
                                                    }
                                                });

                                    } else {
                                        // ── Empty cell
                                        Toast.makeText(
                                                MainActivity.this,
                                                "Mask set! Photo select કરો",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }

                            @Override
                            public void onLongClick(View view, int position) {
                            }
                        }));

        // ── Remove frame click
        btnRemove.setOnClickListener(v -> {

            if (dialogRef[0] != null && dialogRef[0].isShowing()) {
                dialogRef[0].dismiss();
            }

            ImageView cellPhotoIv =
                    findGridCellPhotoIv(gridContainer, cellIndex);

            if (cellPhotoIv != null) {

                cellPhotoIv.setTag(R.id.btn_sticker_gallery, "");

                GradientDrawable restoreGd = new GradientDrawable();
                restoreGd.setColor(Color.parseColor("#DDDDDD"));

                if ("CIRCLE".equals(meta.shape)) {
                    restoreGd.setShape(GradientDrawable.OVAL);
                    cellPhotoIv.setOutlineProvider(
                            new ViewOutlineProvider() {
                                @Override
                                public void getOutline(
                                        View v2,
                                        android.graphics.Outline o) {
                                    o.setOval(0, 0,
                                            v2.getWidth(),
                                            v2.getHeight());
                                }
                            });
                } else if ("ROUNDED".equals(meta.shape)) {
                    float rad = meta.cellSizePx * 0.12f;
                    restoreGd.setCornerRadius(rad);
                    cellPhotoIv.setOutlineProvider(
                            new ViewOutlineProvider() {
                                @Override
                                public void getOutline(
                                        View v2,
                                        android.graphics.Outline o) {
                                    o.setRoundRect(0, 0,
                                            v2.getWidth(),
                                            v2.getHeight(),
                                            meta.cellSizePx * 0.12f);
                                }
                            });
                }

                cellPhotoIv.setBackground(restoreGd);
                cellPhotoIv.setImageDrawable(null);
                cellPhotoIv.setClipToOutline(true);

                exportToJson();
                Toast.makeText(this,
                        "✅ Frame removed!",
                        Toast.LENGTH_SHORT).show();
            }
        });

        dialogRef[0].show();
    }

    // ── Full photo view dialog
    private void showCellFullPhotoDialog(
            JSONObject obj,
            String name,
            String info,
            String shape) {

        android.app.Dialog photoDialog =
                new android.app.Dialog(
                        this,
                        android.R.style
                                .Theme_Black_NoTitleBar_Fullscreen);

        RelativeLayout root = new RelativeLayout(this);
        root.setBackgroundColor(Color.BLACK);
        photoDialog.setContentView(root);

        // ── Photo
        ImageView bigPhoto = new ImageView(this);
        bigPhoto.setScaleType(ImageView.ScaleType.FIT_CENTER);
        bigPhoto.setBackgroundColor(Color.BLACK);

        RelativeLayout.LayoutParams photoLp =
                new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.MATCH_PARENT,
                        RelativeLayout.LayoutParams.MATCH_PARENT);
        root.addView(bigPhoto, photoLp);

        // ── Load photo
        String b64 = obj.optString("photoBitmap", "");
        String uri = obj.optString("photoUri", "");

        if (!b64.isEmpty()) {
            Bitmap bmp = base64ToBitmap(b64);
            if (bmp != null) bigPhoto.setImageBitmap(bmp);
        } else if (!uri.isEmpty()) {
            Glide.with(this)
                    .load(Uri.parse(uri))
                    .into(bigPhoto);
        } else {
            bigPhoto.setImageResource(
                    android.R.drawable.ic_menu_gallery);
        }

        // ── Info overlay at bottom
        LinearLayout infoBar = new LinearLayout(this);
        infoBar.setOrientation(LinearLayout.VERTICAL);
        infoBar.setBackgroundColor(
                Color.parseColor("#AA000000"));
        infoBar.setPadding(24, 16, 24, 40);

        RelativeLayout.LayoutParams infoLp =
                new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.MATCH_PARENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT);
        infoLp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        root.addView(infoBar, infoLp);

        TextView tvName = new TextView(this);
        tvName.setText(name.isEmpty()
                ? "(No Name)" : name);
        tvName.setTextColor(Color.WHITE);
        tvName.setTextSize(20);
        tvName.setTypeface(null, Typeface.BOLD);
        infoBar.addView(tvName);

        TextView tvInfo = new TextView(this);
        tvInfo.setText(info.isEmpty() ? "—" : info);
        tvInfo.setTextColor(
                Color.parseColor("#FF9800"));
        tvInfo.setTextSize(16);
        tvInfo.setTypeface(null, Typeface.BOLD);
        infoBar.addView(tvInfo);

        // ── Close button top right
        TextView btnClose = new TextView(this);
        btnClose.setText("✕");
        btnClose.setTextColor(Color.WHITE);
        btnClose.setTextSize(24);
        btnClose.setPadding(
                dpToPx(16), dpToPx(44),
                dpToPx(16), dpToPx(16));

        RelativeLayout.LayoutParams closeLp =
                new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT);
        closeLp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        closeLp.addRule(RelativeLayout.ALIGN_PARENT_END);
        root.addView(btnClose, closeLp);

        btnClose.setOnClickListener(
                v -> photoDialog.dismiss());

        // ── Tap anywhere to close
        bigPhoto.setOnClickListener(
                v -> photoDialog.dismiss());

        photoDialog.show();
    }

    // ── Find grid cell photo ImageView
    private ImageView findGridCellPhotoIv(
            RelativeLayout gridContainer,
            int cellIndex) {

        if (cellIndex < 0 ||
                cellIndex >= gridContainer.getChildCount())
            return null;

        View cellView = gridContainer.getChildAt(cellIndex);
        if (!(cellView instanceof LinearLayout)) return null;

        LinearLayout cell = (LinearLayout) cellView;
        for (int k = 0; k < cell.getChildCount(); k++) {
            View child = cell.getChildAt(k);
            Object tag = child.getTag(R.id.btn_set_background);
            if (tag != null &&
                    tag.toString().startsWith("GRID_CELL_")) {
                return (ImageView) child;
            }
        }
        return null;
    }

    // ── Find grid cell TextView (name/info)
    private TextView findGridCellTextView(
            RelativeLayout gridContainer,
            int cellIndex,
            String tagPrefix) {

        if (cellIndex < 0 ||
                cellIndex >= gridContainer.getChildCount())
            return null;

        View cellView = gridContainer.getChildAt(cellIndex);
        if (!(cellView instanceof LinearLayout)) return null;

        LinearLayout cell = (LinearLayout) cellView;
        for (int k = 0; k < cell.getChildCount(); k++) {
            View child = cell.getChildAt(k);
            Object tag = child.getTag(R.id.btn_ms_select_all);
            if (tag != null &&
                    tag.toString().startsWith(tagPrefix)) {
                return (TextView) child;
            }
        }
        return null;
    }

    // ── Export CSV
    private void exportGridToCsv(GridMeta meta) {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("No,Name,Percentage,Photo\n");

            for (int i = 0;
                 i < meta.cellDataList.size(); i++) {
                JSONObject obj = meta.cellDataList.get(i);
                String n = obj.optString("name", "");
                String inf = obj.optString("info", "");
                String uri = obj.optString("photoUri", "");
                String b64 = obj.optString(
                        "photoBitmap", "");

                boolean hasPhoto =
                        !uri.isEmpty() || !b64.isEmpty();

                sb.append((i + 1)).append(",")
                        .append(n).append(",")
                        .append(inf).append(",")
                        .append(hasPhoto ? "Yes" : "No")
                        .append("\n");
            }

            String fileName = "grid_list_" +
                    System.currentTimeMillis() + ".csv";

            File file = new File(
                    getExternalFilesDir(null), fileName);

            java.io.FileWriter fw =
                    new java.io.FileWriter(file);
            fw.write(sb.toString());
            fw.close();

            // ── Share
            android.net.Uri fileUri =
                    androidx.core.content.FileProvider
                            .getUriForFile(
                                    this,
                                    getPackageName() + ".provider",
                                    file);

            Intent shareIntent = new Intent(
                    Intent.ACTION_SEND);
            shareIntent.setType("text/csv");
            shareIntent.putExtra(
                    Intent.EXTRA_STREAM, fileUri);
            shareIntent.addFlags(
                    Intent.FLAG_GRANT_READ_URI_PERMISSION);

            startActivity(Intent.createChooser(
                    shareIntent, "CSV Share"));

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this,
                    "CSV export ભૂલ: " + e.getMessage(),
                    Toast.LENGTH_SHORT).show();
        }
    }

    // ── Bulk edit dialog
    private void showGridBulkEditDialog(
            RelativeLayout gridContainer,
            GridMeta meta,
            android.app.Dialog parentDialog) {

        AlertDialog.Builder b =
                new AlertDialog.Builder(this);
        b.setTitle("✏ Bulk Edit");

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(24, 20, 24, 16);

        TextView lbl = new TextView(this);
        lbl.setText("Info (%) prefix/suffix add:");
        lbl.setTextSize(13);
        root.addView(lbl);

        android.widget.EditText etSuffix =
                new android.widget.EditText(this);
        etSuffix.setHint("e.g. % or nothing");
        etSuffix.setText("%");
        root.addView(etSuffix);

        TextView lbl2 = new TextView(this);
        lbl2.setText("Name prefix add (optional):");
        lbl2.setTextSize(13);
        lbl2.setPadding(0, 12, 0, 4);
        root.addView(lbl2);

        android.widget.EditText etPrefix =
                new android.widget.EditText(this);
        etPrefix.setHint("e.g. Mr. or blank");
        root.addView(etPrefix);

        b.setView(root);
        b.setPositiveButton("Apply", (d2, w2) -> {
            String suffix = etSuffix
                    .getText().toString();
            String prefix = etPrefix
                    .getText().toString();

            for (JSONObject obj : meta.cellDataList) {
                try {
                    // Info suffix
                    if (!suffix.isEmpty()) {
                        String inf =
                                obj.optString("info", "");
                        if (!inf.isEmpty() &&
                                !inf.endsWith(suffix)) {
                            obj.put("info", inf + suffix);
                        }
                    }
                    // Name prefix
                    if (!prefix.isEmpty()) {
                        String nm =
                                obj.optString("name", "");
                        if (!nm.isEmpty() &&
                                !nm.startsWith(prefix)) {
                            obj.put("name",
                                    prefix + nm);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            rebuildGridFrame(
                    gridContainer,
                    meta.rows, meta.cols,
                    meta.shape, meta.cellSizePx,
                    meta.showName, meta.showInfo,
                    meta.cellDataList);
            exportToJson();
            parentDialog.dismiss();
            Toast.makeText(this,
                    "✅ Bulk edit done!",
                    Toast.LENGTH_SHORT).show();
        });
        b.setNegativeButton("Cancel", null);
        b.show();
    }


    private void showGridEditPopup(RelativeLayout gridContainer) {

        Object tag = gridContainer.getTag(R.id.btn_grid_frame);
        if (!(tag instanceof GridMeta)) {
            Toast.makeText(this, "Grid edit data મળ્યો નહીં", Toast.LENGTH_SHORT).show();
            return;
        }

        GridMeta meta = (GridMeta) tag;

        if (gridEditPopup != null && gridEditPopup.isShowing()) {
            gridEditPopup.dismiss();
        }

        View popupView = LayoutInflater.from(this)
                .inflate(R.layout.popup_grid_edit, null);

        TextView tvInfo = popupView.findViewById(R.id.tv_grid_info);

        EditText etDeleteNumber = popupView.findViewById(R.id.et_grid_delete_number);
        TextView btnDeleteCellNumber = popupView.findViewById(R.id.btn_grid_delete_cell_number);
        TextView btnCellPhoto = popupView.findViewById(R.id.btn_grid_cell_photo);
  /*      TextView btnSizeMinus = popupView.findViewById(R.id.btn_grid_size_minus);
        TextView btnSizePlus = popupView.findViewById(R.id.btn_grid_size_plus);
        TextView btnRowMinus = popupView.findViewById(R.id.btn_grid_row_minus);
        TextView btnRowPlus = popupView.findViewById(R.id.btn_grid_row_plus);
        TextView btnColMinus = popupView.findViewById(R.id.btn_grid_col_minus);
        TextView btnColPlus = popupView.findViewById(R.id.btn_grid_col_plus);*/
        TextView btnFullEdit = popupView.findViewById(R.id.btn_grid_full_edit);
        TextView btnDeleteGrid = popupView.findViewById(R.id.btn_grid_delete);
        TextView btnClose = popupView.findViewById(R.id.btn_grid_close);
        /*TextView btnMultiPhoto = popupView.findViewById(R.id.btn_grid_multi_photo);*/

        // ── btnClose ની ઉપર ઉમેરો
        TextView btnMoveUp = popupView.findViewById(R.id.btn_grid_move_up);
        TextView btnMoveDown = popupView.findViewById(R.id.btn_grid_move_down);
        TextView btnMoveLeft = popupView.findViewById(R.id.btn_grid_move_left);
        TextView btnMoveRight = popupView.findViewById(R.id.btn_grid_move_right);

        TextView btnTransfer = popupView.findViewById(R.id.btn_grid_transfer);

        TextView btnSwap = popupView.findViewById(R.id.btn_grid_swap);

        TextView btnGridFrame = popupView.findViewById(R.id.btn_grid_cell_frame);

        TextView btnEditCellFrame = popupView.findViewById(R.id.btn_grid_edit_cell_frame);

        // showGridEditPopup() માં ઉમેરો
        TextView btnListView = popupView.findViewById(
                R.id.btn_grid_list_view);
        if (btnListView != null) {
            btnListView.setOnClickListener(v -> {
                if (gridEditPopup != null &&
                        gridEditPopup.isShowing()) {
                    gridEditPopup.dismiss();
                }
                showGridListDialog(gridContainer);
            });
        }


        if (btnEditCellFrame != null) {
            btnEditCellFrame.setOnClickListener(v -> {
                if (gridEditPopup != null && gridEditPopup.isShowing()) {
                    gridEditPopup.dismiss();
                }
                showGridCellMaskEditDialog(gridContainer);
            });
        }


        if (btnGridFrame != null) {
            btnGridFrame.setOnClickListener(v -> {
                if (gridEditPopup != null && gridEditPopup.isShowing()) {
                    gridEditPopup.dismiss();
                }
                showGridCellFramePicker(gridContainer);
            });
        }

        if (btnSwap != null) {
            btnSwap.setText(isGridSwapMode ? "✅ Swap ON — Cancel" : "⇄ Cell Swap Mode");
            btnSwap.setBackgroundColor(isGridSwapMode
                    ? Color.parseColor("#C62828")
                    : Color.parseColor("#E65100"));

            btnSwap.setOnClickListener(v -> {
                isGridSwapMode = !isGridSwapMode;

                // Reset any pending selection
                selectedSwapCell = null;
                selectedSwapCellIdx = -1;
                selectedSwapDataList = null;
                selectedSwapGrid = null;

                if (gridEditPopup != null && gridEditPopup.isShowing()) {
                    gridEditPopup.dismiss();
                }

                Toast.makeText(this,
                        isGridSwapMode
                                ? "✅ Swap Mode ON — Cell tap = select, ફરી tap = swap"
                                : "Swap mode OFF",
                        Toast.LENGTH_LONG).show();
            });
        }

        if (btnTransfer != null) {
            btnTransfer.setOnClickListener(v -> {
                if (gridEditPopup != null && gridEditPopup.isShowing()) {
                    gridEditPopup.dismiss();
                }
                showGridTransferDialog(gridContainer);
            });
        }

        if (btnMoveUp != null) {
            btnMoveUp.setOnClickListener(v -> {
                gridContainer.setY(gridContainer.getY() - 20);
                exportToJson();
            });
            btnMoveUp.setOnLongClickListener(v -> {
                gridContainer.setY(gridContainer.getY() - 80);
                exportToJson();
                return true;
            });
        }
        if (btnMoveDown != null) {
            btnMoveDown.setOnClickListener(v -> {
                gridContainer.setY(gridContainer.getY() + 20);
                exportToJson();
            });
            btnMoveDown.setOnLongClickListener(v -> {
                gridContainer.setY(gridContainer.getY() + 80);
                exportToJson();
                return true;
            });
        }
        if (btnMoveLeft != null) {
            btnMoveLeft.setOnClickListener(v -> {
                gridContainer.setX(gridContainer.getX() - 20);
                exportToJson();
            });
            btnMoveLeft.setOnLongClickListener(v -> {
                gridContainer.setX(gridContainer.getX() - 80);
                exportToJson();
                return true;
            });
        }
        if (btnMoveRight != null) {
            btnMoveRight.setOnClickListener(v -> {
                gridContainer.setX(gridContainer.getX() + 20);
                exportToJson();
            });
            btnMoveRight.setOnLongClickListener(v -> {
                gridContainer.setX(gridContainer.getX() + 80);
                exportToJson();
                return true;
            });
        }

        tvInfo.setText("Rows: " + meta.rows
                + "  Cols: " + meta.cols
                + "  Size: " + meta.cellSizePx);

        gridEditPopup = new PopupWindow(
                popupView,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                false
        );

        gridEditPopup.setTouchable(true);
        gridEditPopup.setOutsideTouchable(false);
        gridEditPopup.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        btnDeleteCellNumber.setOnClickListener(v -> {

            GridMeta m = (GridMeta) gridContainer.getTag(R.id.btn_grid_frame);
            if (m == null || m.cellDataList == null) return;

            String numStr = etDeleteNumber.getText().toString().trim();

            if (numStr.isEmpty()) {
                Toast.makeText(this, "Cell number નાખો", Toast.LENGTH_SHORT).show();
                return;
            }

            int cellNo;

            try {
                cellNo = Integer.parseInt(numStr);
            } catch (Exception e) {
                Toast.makeText(this, "સાચો number નાખો", Toast.LENGTH_SHORT).show();
                return;
            }

            int cellIndex = cellNo - 1;
            int totalCells = m.rows * m.cols;

            if (cellIndex < 0 || cellIndex >= totalCells) {
                Toast.makeText(this,
                        "Cell number 1 થી " + totalCells + " વચ્ચે હોવો જોઈએ",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                JSONObject obj = m.cellDataList.get(cellIndex);
                obj.put("photoUri", "");
                obj.put("name", "");
                obj.put("info", "");
            } catch (Exception e) {
                e.printStackTrace();
            }

            // ── Remove trailing empty cells
            for (int idx = m.cellDataList.size() - 1; idx >= 0; idx--) {
                JSONObject c = m.cellDataList.get(idx);
                boolean isEmpty = c.optString("photoUri", "").isEmpty()
                        && c.optString("photoBitmap", "").isEmpty()
                        && c.optString("name", "").isEmpty()
                        && c.optString("info", "").isEmpty();
                if (isEmpty) {
                    m.cellDataList.remove(idx);
                } else {
                    break;
                }
            }

            int newTotal = m.cellDataList.size();
            int newRows = newTotal == 0 ? 1
                    : (int) Math.ceil((float) newTotal / m.cols);

            rebuildGridFrame(
                    gridContainer,
                    newRows,
                    m.cols,
                    m.shape,
                    m.cellSizePx,
                    m.showName,
                    m.showInfo,
                    m.cellDataList
            );
            selectedGridPhotoForPopup = null;
            selectedGridCellIndexForPopup = -1;
            selectedGridDataListForPopup = null;

            Toast.makeText(this,
                    "✅ Cell " + cellNo + " delete થઈ ગયો",
                    Toast.LENGTH_SHORT).show();

            gridContainer.postDelayed(() -> showGridEditPopup(gridContainer), 80);
        });

     /*   btnMultiPhoto.setOnClickListener(v -> {

            GridMeta m = (GridMeta) gridContainer.getTag(R.id.btn_grid_frame);

            if (m == null || m.cellDataList == null) {
                Toast.makeText(this, "Grid data મળ્યો નહીં", Toast.LENGTH_SHORT).show();
                return;
            }

            pendingGridMultiContainer = gridContainer;

            // જો કોઈ cell selected હોય તો ત્યાંથી photos set થશે,
            // નહિ તો 0 થી શરૂ થશે.
            if (selectedGridCellIndexForPopup >= 0) {
                pendingGridMultiStartIdx = selectedGridCellIndexForPopup;
            } else {
                pendingGridMultiStartIdx = 0;
            }

            pendingGridMultiDataList = m.cellDataList;
            pendingGridMultiShape = m.shape;
            pendingGridMultiCellSize = m.cellSizePx;

            if (gridEditPopup != null && gridEditPopup.isShowing()) {
                gridEditPopup.dismiss();
            }

            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.setType("image/*");
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);

            startActivityForResult(
                    Intent.createChooser(intent, "Multiple Photos Select"),
                    REQUEST_GRID_MULTI_PHOTO
            );
        });*/

        btnCellPhoto.setOnClickListener(v -> {
            if (selectedGridPhotoForPopup == null ||
                    selectedGridCellIndexForPopup < 0 ||
                    selectedGridDataListForPopup == null) {

                Toast.makeText(this, "પહેલા grid cell select કરો", Toast.LENGTH_SHORT).show();
                return;
            }

            pendingGridCellTarget = selectedGridPhotoForPopup;
            pendingGridCellIdx = selectedGridCellIndexForPopup;
            pendingGridCellDataList = selectedGridDataListForPopup;
            pendingGridShape = selectedGridShapeForPopup;
            pendingGridCellSize = selectedGridCellSizeForPopup;

            if (gridEditPopup != null && gridEditPopup.isShowing()) {
                gridEditPopup.dismiss();
            }

            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(
                    Intent.createChooser(intent, "Photo Select"),
                    REQUEST_GRID_CELL_PHOTO
            );
        });

       /* btnSizeMinus.setOnClickListener(v -> {
            GridMeta m = (GridMeta) gridContainer.getTag(R.id.btn_grid_frame);
            if (m == null) return;

            int newSize = Math.max(60, m.cellSizePx - 20);

            rebuildGridFrame(
                    gridContainer,
                    m.rows,
                    m.cols,
                    m.shape,
                    newSize,
                    m.showName,
                    m.showInfo,
                    m.cellDataList
            );

            gridContainer.postDelayed(() -> showGridEditPopup(gridContainer), 80);
        });

        btnSizePlus.setOnClickListener(v -> {
            GridMeta m = (GridMeta) gridContainer.getTag(R.id.btn_grid_frame);
            if (m == null) return;

            int newSize = Math.min(1000, m.cellSizePx + 20);

            rebuildGridFrame(
                    gridContainer,
                    m.rows,
                    m.cols,
                    m.shape,
                    newSize,
                    m.showName,
                    m.showInfo,
                    m.cellDataList
            );

            gridContainer.postDelayed(() -> showGridEditPopup(gridContainer), 80);
        });

        btnRowMinus.setOnClickListener(v -> {
            GridMeta m = (GridMeta) gridContainer.getTag(R.id.btn_grid_frame);
            if (m == null) return;

            if (m.rows <= 1) {
                Toast.makeText(this, "Minimum 1 row જોઈએ", Toast.LENGTH_SHORT).show();
                return;
            }

            rebuildGridFrame(
                    gridContainer,
                    m.rows - 1,
                    m.cols,
                    m.shape,
                    m.cellSizePx,
                    m.showName,
                    m.showInfo,
                    m.cellDataList
            );

            gridContainer.postDelayed(() -> showGridEditPopup(gridContainer), 80);
        });

        btnRowPlus.setOnClickListener(v -> {
            GridMeta m = (GridMeta) gridContainer.getTag(R.id.btn_grid_frame);
            if (m == null) return;

            rebuildGridFrame(
                    gridContainer,
                    m.rows + 1,
                    m.cols,
                    m.shape,
                    m.cellSizePx,
                    m.showName,
                    m.showInfo,
                    m.cellDataList
            );

            gridContainer.postDelayed(() -> showGridEditPopup(gridContainer), 80);
        });

        btnColMinus.setOnClickListener(v -> {
            GridMeta m = (GridMeta) gridContainer.getTag(R.id.btn_grid_frame);
            if (m == null) return;

            if (m.cols <= 1) {
                Toast.makeText(this, "Minimum 1 column જોઈએ", Toast.LENGTH_SHORT).show();
                return;
            }

            rebuildGridFrame(
                    gridContainer,
                    m.rows,
                    m.cols - 1,
                    m.shape,
                    m.cellSizePx,
                    m.showName,
                    m.showInfo,
                    m.cellDataList
            );

            gridContainer.postDelayed(() -> showGridEditPopup(gridContainer), 80);
        });

        btnColPlus.setOnClickListener(v -> {
            GridMeta m = (GridMeta) gridContainer.getTag(R.id.btn_grid_frame);
            if (m == null) return;

            rebuildGridFrame(
                    gridContainer,
                    m.rows,
                    m.cols + 1,
                    m.shape,
                    m.cellSizePx,
                    m.showName,
                    m.showInfo,
                    m.cellDataList
            );

            gridContainer.postDelayed(() -> showGridEditPopup(gridContainer), 80);
        });
*/
        btnFullEdit.setOnClickListener(v -> {
            if (gridEditPopup != null && gridEditPopup.isShowing()) {
                gridEditPopup.dismiss();
            }
            showGridFullEditDialog(gridContainer);
        });

        btnDeleteGrid.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Delete Grid")
                    .setMessage("આ આખી grid delete કરવી છે?")
                    .setPositiveButton("Delete", (dialog, which) -> {

                        if (gridEditPopup != null && gridEditPopup.isShowing()) {
                            gridEditPopup.dismiss();
                        }

                        mainLayout.removeView(gridContainer);

                        if (currentlySelectedGrid == gridContainer) {
                            currentlySelectedGrid = null;
                        }

                        if (currentlySelectedView == gridContainer) {
                            currentlySelectedView = null;
                        }

                        selectedGridPhotoForPopup = null;
                        selectedGridCellIndexForPopup = -1;
                        selectedGridDataListForPopup = null;

                        exportToJson();

                        Toast.makeText(this, "✅ Grid delete થઈ ગઈ", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });

        btnClose.setOnClickListener(v -> {
            if (gridEditPopup != null && gridEditPopup.isShowing()) {
                gridEditPopup.dismiss();
            }
        });

        int popupX = (int) (gridContainer.getX() + 20);
        int popupY = (int) (gridContainer.getY() + 20);

        gridEditPopup.showAtLocation(mainLayout, Gravity.NO_GRAVITY, popupX, popupY);
    }

    private void showGridCellMaskEditDialog(RelativeLayout gridContainer) {
        Object tag = gridContainer.getTag(R.id.btn_grid_frame);
        if (!(tag instanceof GridMeta)) return;
        GridMeta meta = (GridMeta) tag;

        // ── Selected cell check
        if (selectedGridPhotoForPopup == null || selectedGridCellIndexForPopup < 0) {
            Toast.makeText(this, "પહેલા cell tap કરો (select)", Toast.LENGTH_SHORT).show();
            return;
        }

        final ImageView targetCell = selectedGridPhotoForPopup;
        final int cellIdx = selectedGridCellIndexForPopup;

        // ── Current frame tag check
        Object frameTag = targetCell.getTag(R.id.btn_sticker_gallery);
        String frameTagStr = frameTag != null ? frameTag.toString() : "";

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Cell " + (cellIdx + 1) + " — Mask/Frame Edit");

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(24, 20, 24, 16);

        // ── Current frame info
        TextView lblInfo = new TextView(this);
        lblInfo.setText(frameTagStr.contains("|||")
                ? "✅ Frame set છે — Replace કરો"
                : "⚠ Frame set નથી");
        lblInfo.setTextSize(13);
        lblInfo.setTextColor(Color.parseColor("#1565C0"));
        lblInfo.setPadding(0, 0, 0, 12);
        root.addView(lblInfo);

        // ── Preview
        ImageView previewIv = new ImageView(this);
        previewIv.setScaleType(ImageView.ScaleType.FIT_CENTER);
        previewIv.setBackgroundColor(Color.parseColor("#F5F5F5"));
        LinearLayout.LayoutParams prevLp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 180);
        prevLp.setMargins(0, 0, 0, 12);
        previewIv.setLayoutParams(prevLp);

        // Current image show
        if (targetCell.getDrawable() != null) {
            previewIv.setImageDrawable(targetCell.getDrawable());
        }
        root.addView(previewIv);
        // ── 1. New frame picker button
        Button btnPickFrame = new Button(this);
        btnPickFrame.setText("🖼 New Frame/Mask Select");
        btnPickFrame.setTextColor(Color.WHITE);
        btnPickFrame.setBackgroundColor(Color.parseColor("#1565C0"));
        LinearLayout.LayoutParams btnLp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        btnLp.setMargins(0, 0, 0, 8);
        btnPickFrame.setLayoutParams(btnLp);
        root.addView(btnPickFrame);

        // ── 2. Replace photo (with existing frame)
        Button btnReplacePhoto = new Button(this);
        btnReplacePhoto.setText("📷 Photo Replace (Frame Keep)");
        btnReplacePhoto.setTextColor(Color.WHITE);
        btnReplacePhoto.setBackgroundColor(Color.parseColor("#2E7D32"));
        btnReplacePhoto.setLayoutParams(btnLp);
        root.addView(btnReplacePhoto);

        // ── 3. Remove frame
        Button btnRemove = new Button(this);
        btnRemove.setText("✕ Frame Remove");
        btnRemove.setTextColor(Color.WHITE);
        btnRemove.setBackgroundColor(Color.parseColor("#C62828"));
        btnRemove.setLayoutParams(btnLp);
        root.addView(btnRemove);

        // ── Frame RecyclerView (hidden initially)
        ProgressBar progressBar = new ProgressBar(this);
        progressBar.setVisibility(View.GONE);
        root.addView(progressBar);

        RecyclerView frameRV = new RecyclerView(this);
        frameRV.setVisibility(View.GONE);
        LinearLayout.LayoutParams rvLp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 400);
        frameRV.setLayoutParams(rvLp);
        root.addView(frameRV);

        builder.setView(new ScrollView(this) {{
            addView(root);
        }});
        builder.setNegativeButton("Close", null);

        final AlertDialog[] dialogRef = {null};
        dialogRef[0] = builder.create();

        // ── Frame picker button click
        btnPickFrame.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);
            frameRV.setVisibility(View.VISIBLE);
            loadFramesDirectly(frameRV, progressBar);

            frameRV.addOnItemTouchListener(
                    new invite_AppConstants.RecyclerTouchListener(
                            this, frameRV,
                            new invite_AppConstants.RecyclerTouchListener.ClickListener() {

                                @Override
                                public void onClick(View view, int position) {
                                    if (invite_photo_frame.frame_arrayList == null
                                            || invite_photo_frame.frame_arrayList.isEmpty())
                                        return;

                                    String maskUrl = invite_photo_frame.frame_arrayList
                                            .get(position).getcard_background();
                                    String topUrl = invite_photo_frame.frame_arrayList
                                            .get(position).getemail_icon();

                                    if (dialogRef[0] != null) dialogRef[0].dismiss();

                                    // ── Current photo bitmap get
                                    Bitmap currentPhoto = null;
                                    if (targetCell.getDrawable() instanceof
                                            android.graphics.drawable.BitmapDrawable) {
                                        currentPhoto = ((android.graphics.drawable.BitmapDrawable)
                                                targetCell.getDrawable()).getBitmap();
                                    }

                                    if (currentPhoto != null) {
                                        // ── Photo existing — frame replace
                                        final Bitmap finalPhoto = currentPhoto;
                                        Glide.with(MainActivity.this)
                                                .asBitmap()
                                                .load(maskUrl)
                                                .into(new CustomTarget<Bitmap>() {
                                                    @Override
                                                    public void onResourceReady(
                                                            @NonNull Bitmap maskBitmap,
                                                            @Nullable Transition<? super Bitmap> t) {

                                                        // ── Frame tag update
                                                        targetCell.setTag(
                                                                R.id.btn_sticker_gallery,
                                                                maskUrl + "|||" + topUrl);

                                                        applyFrameToCellWithPhoto(
                                                                targetCell,
                                                                finalPhoto,
                                                                maskBitmap,
                                                                topUrl,
                                                                Color.TRANSPARENT,
                                                                meta.cellSizePx);

                                                        // ── DataList update
                                                        try {
                                                            if (selectedGridDataListForPopup != null
                                                                    && cellIdx < selectedGridDataListForPopup.size()) {
                                                                selectedGridDataListForPopup
                                                                        .get(cellIdx)
                                                                        .put("frameMask", maskUrl);
                                                                selectedGridDataListForPopup
                                                                        .get(cellIdx)
                                                                        .put("frameTop", topUrl);
                                                            }
                                                        } catch (Exception e) {
                                                            e.printStackTrace();
                                                        }

                                                        exportToJson();
                                                        Toast.makeText(MainActivity.this,
                                                                "✅ Frame replace થઈ ગઈ!",
                                                                Toast.LENGTH_SHORT).show();
                                                    }

                                                    @Override
                                                    public void onLoadCleared(
                                                            @Nullable Drawable p) {
                                                    }
                                                });
                                    } else {
                                        // ── Empty cell — just frame tag set
                                        targetCell.setTag(
                                                R.id.btn_sticker_gallery,
                                                maskUrl + "|||" + topUrl);

                                        Toast.makeText(MainActivity.this, "Frame set! Photo select કરો",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onLongClick(View view, int position) {
                                }
                            }));
        });

        // ── Replace photo (keep existing frame)
        btnReplacePhoto.setOnClickListener(v -> {
            if (!frameTagStr.contains("|||")) {
                Toast.makeText(this,
                        "પહેલા frame select કરો",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            if (dialogRef[0] != null) dialogRef[0].dismiss();

            // ── Existing frame parts
            String[] parts = frameTagStr.split("\\|\\|\\|");
            String existMask = parts.length > 0 ? parts[0] : "";
            String existTop = parts.length > 1 ? parts[1] : "";

            // ── Photo picker → showCellPhotoAdjustDialog
            pendingGridCellTarget = targetCell;
            pendingGridCellIdx = cellIdx;
            pendingGridCellDataList = selectedGridDataListForPopup;
            pendingGridShape = meta.shape;
            pendingGridCellSize = meta.cellSizePx;

            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(
                    Intent.createChooser(intent, "Photo Select"),
                    REQUEST_GRID_CELL_PHOTO);
        });

        // ── Remove frame
        btnRemove.setOnClickListener(v -> {
            targetCell.setTag(R.id.btn_sticker_gallery, "");

            // ── Original shape restore
            GradientDrawable restoreGd = new GradientDrawable();
            restoreGd.setColor(Color.parseColor("#DDDDDD"));

            if ("CIRCLE".equals(meta.shape)) {
                restoreGd.setShape(GradientDrawable.OVAL);
                targetCell.setOutlineProvider(new ViewOutlineProvider() {
                    @Override
                    public void getOutline(View v2, android.graphics.Outline o) {
                        o.setOval(0, 0, v2.getWidth(), v2.getHeight());
                    }
                });
            } else if ("ROUNDED".equals(meta.shape)) {
                float rad = meta.cellSizePx * 0.12f;
                restoreGd.setCornerRadius(rad);
                targetCell.setOutlineProvider(new ViewOutlineProvider() {
                    @Override
                    public void getOutline(View v2, android.graphics.Outline o) {
                        o.setRoundRect(0, 0, v2.getWidth(), v2.getHeight(),
                                meta.cellSizePx * 0.12f);
                    }
                });
            }

            targetCell.setImageDrawable(null);
            targetCell.setBackground(restoreGd);
            targetCell.setClipToOutline(true);

            if (dialogRef[0] != null) dialogRef[0].dismiss();

            exportToJson();
            Toast.makeText(this,
                    "✅ Frame remove — original shape restore!",
                    Toast.LENGTH_SHORT).show();
        });

        dialogRef[0].show();
    }


    private void showGridTransferDialog(RelativeLayout sourceGrid) {

        // ── Source grid meta
        Object tag = sourceGrid.getTag(R.id.btn_grid_frame);
        if (!(tag instanceof GridMeta)) {
            Toast.makeText(this, "Grid data મળ્યો નહીં",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        GridMeta sourceMeta = (GridMeta) tag;

        // ── Page ના બધા grids find
        List<RelativeLayout> allGrids = new ArrayList<>();
        List<Integer> gridNumbers = new ArrayList<>();

        int gridCount = 0;
        for (int i = 0; i < mainLayout.getChildCount(); i++) {
            View v = mainLayout.getChildAt(i);
            Object bgTag = v.getTag(R.id.btn_set_background);
            if ("GRID_FRAME".equals(bgTag) && v instanceof RelativeLayout) {
                gridCount++;
                allGrids.add((RelativeLayout) v);
                gridNumbers.add(gridCount);
            }
        }

        if (allGrids.size() < 2) {
            Toast.makeText(this,
                    "Transfer માટે page પર ઓછામાં ઓછી 2 grids હોવી જોઈએ",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        // ── Source grid number find
        int sourceGridNum = -1;
        for (int i = 0; i < allGrids.size(); i++) {
            if (allGrids.get(i) == sourceGrid) {
                sourceGridNum = i + 1;
                break;
            }
        }
        final int finalSourceNum = sourceGridNum;

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Grid Transfer");

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(24, 20, 24, 16);

        // ── Info label
        TextView lblInfo = new TextView(this);
        lblInfo.setText("Grid " + sourceGridNum + " → transfer to:");
        lblInfo.setTextSize(14);
        lblInfo.setTextColor(Color.parseColor("#1565C0"));
        lblInfo.setPadding(0, 0, 0, 12);
        root.addView(lblInfo);

        // ── Source cell range
        TextView lblSrcRange = new TextView(this);
        lblSrcRange.setText("Source Cells (Grid " + sourceGridNum + "):");
        lblSrcRange.setTextSize(13);
        lblSrcRange.setTextColor(Color.DKGRAY);
        root.addView(lblSrcRange);

        LinearLayout srcRow = new LinearLayout(this);
        srcRow.setOrientation(LinearLayout.HORIZONTAL);
        srcRow.setGravity(Gravity.CENTER_VERTICAL);
        srcRow.setPadding(0, 4, 0, 12);

        TextView lblFrom = new TextView(this);
        lblFrom.setText("From cell:");
        lblFrom.setTextSize(13);
        lblFrom.setPadding(0, 0, 8, 0);
        srcRow.addView(lblFrom);

        android.widget.NumberPicker npSrcFrom =
                new android.widget.NumberPicker(this);
        npSrcFrom.setMinValue(1);
        npSrcFrom.setMaxValue(
                Math.max(1, sourceMeta.rows * sourceMeta.cols));
        npSrcFrom.setValue(1);
        srcRow.addView(npSrcFrom);

        TextView lblTo = new TextView(this);
        lblTo.setText("  To:");
        lblTo.setTextSize(13);
        lblTo.setPadding(8, 0, 8, 0);
        srcRow.addView(lblTo);

        android.widget.NumberPicker npSrcTo =
                new android.widget.NumberPicker(this);
        npSrcTo.setMinValue(1);
        npSrcTo.setMaxValue(
                Math.max(1, sourceMeta.rows * sourceMeta.cols));
        npSrcTo.setValue(
                Math.max(1, sourceMeta.rows * sourceMeta.cols));
        srcRow.addView(npSrcTo);

        root.addView(srcRow);

        // ── Target grid select
        TextView lblTarget = new TextView(this);
        lblTarget.setText("Target Grid:");
        lblTarget.setTextSize(13);
        lblTarget.setTextColor(Color.DKGRAY);
        root.addView(lblTarget);

        // ── Grid buttons
        LinearLayout gridBtnRow = new LinearLayout(this);
        gridBtnRow.setOrientation(LinearLayout.HORIZONTAL);
        gridBtnRow.setPadding(0, 4, 0, 8);

        final int[] selectedTargetIdx = {-1};
        final Button[] gridBtns = new Button[allGrids.size()];

        for (int i = 0; i < allGrids.size(); i++) {
            final int gIdx = i;
            final int gNum = i + 1;

            Button gb = new Button(this);
            gb.setText("Grid " + gNum);
            gb.setTextSize(12);
            gb.setTextColor(Color.WHITE);

            // Source grid = disabled
            if (gNum == finalSourceNum) {
                gb.setBackgroundColor(Color.GRAY);
                gb.setEnabled(false);
            } else {
                gb.setBackgroundColor(Color.parseColor("#90CAF9"));
                gb.setOnClickListener(bv -> {
                    selectedTargetIdx[0] = gIdx;
                    for (Button btn : gridBtns) {
                        if (btn != null && btn.isEnabled()) {
                            btn.setBackgroundColor(
                                    Color.parseColor("#90CAF9"));
                        }
                    }
                    gb.setBackgroundColor(
                            Color.parseColor("#1565C0"));
                });
            }

            LinearLayout.LayoutParams gbLp =
                    new LinearLayout.LayoutParams(
                            0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
            gbLp.setMargins(3, 0, 3, 0);
            gb.setLayoutParams(gbLp);
            gridBtns[i] = gb;
            gridBtnRow.addView(gb);
        }
        root.addView(gridBtnRow);

        // ── Target start cell
        TextView lblDstCell = new TextView(this);
        lblDstCell.setText("Target Start Cell:");
        lblDstCell.setTextSize(13);
        lblDstCell.setTextColor(Color.DKGRAY);
        lblDstCell.setPadding(0, 8, 0, 4);
        root.addView(lblDstCell);

        android.widget.NumberPicker npDstStart =
                new android.widget.NumberPicker(this);
        npDstStart.setMinValue(1);
        npDstStart.setMaxValue(100); // dynamic update on select
        npDstStart.setValue(1);
        root.addView(npDstStart);

        // ── Transfer mode
        TextView lblMode = new TextView(this);
        lblMode.setText("Transfer Mode:");
        lblMode.setTextSize(13);
        lblMode.setTextColor(Color.DKGRAY);
        lblMode.setPadding(0, 12, 0, 4);
        root.addView(lblMode);

        final String[] transferMode = {"PHOTO_NAME_INFO"};
        LinearLayout modeRow = new LinearLayout(this);
        modeRow.setOrientation(LinearLayout.HORIZONTAL);

        String[] modeLabels = {"Photo+Name+Info", "Photo Only", "Name+Info Only"};
        String[] modeKeys = {"PHOTO_NAME_INFO", "PHOTO_ONLY", "TEXT_ONLY"};
        Button[] modeBtns = new Button[3];

        for (int i = 0; i < modeLabels.length; i++) {
            final int mIdx = i;
            Button mb = new Button(this);
            mb.setText(modeLabels[i]);
            mb.setTextSize(10);
            mb.setTextColor(Color.WHITE);
            mb.setBackgroundColor(i == 0
                    ? Color.parseColor("#1565C0")
                    : Color.parseColor("#90CAF9"));
            LinearLayout.LayoutParams mbLp =
                    new LinearLayout.LayoutParams(
                            0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
            mbLp.setMargins(3, 0, 3, 0);
            mb.setLayoutParams(mbLp);
            modeBtns[i] = mb;
            mb.setOnClickListener(mv -> {
                transferMode[0] = modeKeys[mIdx];
                for (Button b : modeBtns) {
                    b.setBackgroundColor(Color.parseColor("#90CAF9"));
                }
                mb.setBackgroundColor(Color.parseColor("#1565C0"));
            });
            modeRow.addView(mb);
        }
        root.addView(modeRow);

        builder.setView(new ScrollView(this) {{
            addView(root);
        }});

        builder.setPositiveButton("Transfer", (d, w) -> {

            if (selectedTargetIdx[0] < 0) {
                Toast.makeText(this, "Target grid select કરો",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            RelativeLayout targetGrid = allGrids.get(selectedTargetIdx[0]);
            Object tTag = targetGrid.getTag(R.id.btn_grid_frame);
            if (!(tTag instanceof GridMeta)) {
                Toast.makeText(this, "Target grid data મળ્યો નહીં",
                        Toast.LENGTH_SHORT).show();
                return;
            }
            GridMeta targetMeta = (GridMeta) tTag;

            int srcFrom = npSrcFrom.getValue() - 1; // 0-based
            int srcTo = npSrcTo.getValue() - 1;     // 0-based
            int dstStart = npDstStart.getValue() - 1; // 0-based

            if (srcFrom > srcTo) {
                Toast.makeText(this,
                        "From cell > To cell — values fix કરો",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            // ── Transfer cells
            int count = 0;
            int srcIdx = srcFrom;
            int dstIdx = dstStart;

            int srcTotal = sourceMeta.cellDataList != null
                    ? sourceMeta.cellDataList.size() : 0;
            int dstTotal = targetMeta.cellDataList != null
                    ? targetMeta.cellDataList.size() : 0;

            while (srcIdx <= srcTo
                    && srcIdx < srcTotal
                    && dstIdx < dstTotal) {

                JSONObject srcCell =
                        sourceMeta.cellDataList.get(srcIdx);
                JSONObject dstCell =
                        targetMeta.cellDataList.get(dstIdx);

                try {
                    // Photo transfer
                    if (!transferMode[0].equals("TEXT_ONLY")) {
                        dstCell.put("photoUri",
                                srcCell.optString("photoUri", ""));
                        if (srcCell.has("photoBitmap")) {
                            dstCell.put("photoBitmap",
                                    srcCell.optString("photoBitmap", ""));
                        }
                    }

                    // Name + Info transfer
                    if (!transferMode[0].equals("PHOTO_ONLY")) {
                        dstCell.put("name",
                                srcCell.optString("name", ""));
                        dstCell.put("info",
                                srcCell.optString("info", ""));
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

                srcIdx++;
                dstIdx++;
                count++;
            }

            // ── Rebuild target grid with new data
            rebuildGridFrame(
                    targetGrid,
                    targetMeta.rows,
                    targetMeta.cols,
                    targetMeta.shape,
                    targetMeta.cellSizePx,
                    targetMeta.showName,
                    targetMeta.showInfo,
                    targetMeta.cellDataList
            );

            exportToJson();

            Toast.makeText(this,
                    "✅ " + count + " cells Grid "
                            + finalSourceNum + " → Grid "
                            + (selectedTargetIdx[0] + 1)
                            + " transfer થઈ ગઈ!",
                    Toast.LENGTH_LONG).show();
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void showGridFullEditDialog(RelativeLayout gridContainer) {

        Object tag = gridContainer.getTag(R.id.btn_grid_frame);
        if (!(tag instanceof GridMeta)) return;

        GridMeta meta = (GridMeta) tag;

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Grid Full Edit");

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(24, 20, 24, 16);

        TextView lblRows = new TextView(this);
        lblRows.setText("Rows:");
        root.addView(lblRows);

        NumberPicker npRows = new NumberPicker(this);
        npRows.setMinValue(1);
        npRows.setMaxValue(20);
        npRows.setValue(meta.rows);
        root.addView(npRows);

        TextView lblCols = new TextView(this);
        lblCols.setText("Columns:");
        root.addView(lblCols);

        NumberPicker npCols = new NumberPicker(this);
        npCols.setMinValue(1);
        npCols.setMaxValue(20);
        npCols.setValue(meta.cols);
        root.addView(npCols);

        TextView lblSize = new TextView(this);
        lblSize.setText("Cell Size: " + meta.cellSizePx + "px");
        root.addView(lblSize);

        SeekBar seekSize = new SeekBar(this);
        seekSize.setMin(80);
        seekSize.setMax(600);
        seekSize.setProgress(meta.cellSizePx);
        root.addView(seekSize);

        final int[] selectedSize = {meta.cellSizePx};

        seekSize.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                selectedSize[0] = progress;
                lblSize.setText("Cell Size: " + progress + "px");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        builder.setView(root);

        builder.setPositiveButton("Apply", (dialog, which) -> {
            GridMeta m = (GridMeta) gridContainer.getTag(R.id.btn_grid_frame);

            rebuildGridFrame(
                    gridContainer,
                    npRows.getValue(),
                    npCols.getValue(),
                    m.shape,
                    selectedSize[0],
                    m.showName,
                    m.showInfo,
                    m.cellDataList
            );
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void rebuildGridFrame(RelativeLayout gridContainer,
                                  int rows,
                                  int cols,
                                  String shape,
                                  int cellSizePx,
                                  boolean showName,
                                  boolean showInfo,
                                  List<JSONObject> oldDataList) {

        float oldX = gridContainer.getX();
        float oldY = gridContainer.getY();

        gridContainer.removeAllViews();

        int gap = 8;
        int nameH = showName ? 40 : 0;
        int infoH = showInfo ? 30 : 0;
        int cellTotalH = cellSizePx + nameH + infoH;

        int totalW = cols * cellSizePx + (cols - 1) * gap;
        int totalH = rows * cellTotalH + (rows - 1) * gap;

        RelativeLayout.LayoutParams outerLp = new RelativeLayout.LayoutParams(totalW, totalH);
        gridContainer.setLayoutParams(outerLp);
        gridContainer.setX(oldX);
        gridContainer.setY(oldY);

        ArrayList<JSONObject> newDataList = new ArrayList<>();
        int totalCells = rows * cols;

        for (int i = 0; i < totalCells; i++) {
            JSONObject obj;
            if (oldDataList != null && i < oldDataList.size()) {
                obj = oldDataList.get(i);
            } else {
                obj = new JSONObject();
                try {
                    obj.put("photoUri", "");
                    obj.put("photoBitmap", "");
                    obj.put("name", "Name " + (i + 1));
                    obj.put("info", "0.00%");
                    obj.put("frameMask", "");
                    obj.put("frameTop", "");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            newDataList.add(obj);
        }

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {

                final int cellIdx = r * cols + c;
                final JSONObject cellObj = newDataList.get(cellIdx);

                // ── Cell layout ──────────────────────────────────
                LinearLayout cell = new LinearLayout(this);
                cell.setOrientation(LinearLayout.VERTICAL);
                cell.setGravity(Gravity.CENTER_HORIZONTAL);

                RelativeLayout.LayoutParams cellLp =
                        new RelativeLayout.LayoutParams(cellSizePx, cellTotalH);
                cellLp.leftMargin = c * (cellSizePx + gap);
                cellLp.topMargin = r * (cellTotalH + gap);
                cell.setLayoutParams(cellLp);

                // ── Photo ImageView ──────────────────────────────
                ImageView photoIv = new ImageView(this);
                LinearLayout.LayoutParams photoLp =
                        new LinearLayout.LayoutParams(cellSizePx, cellSizePx);
                photoIv.setLayoutParams(photoLp);
                photoIv.setScaleType(ImageView.ScaleType.CENTER_CROP);

                GradientDrawable photoGd = new GradientDrawable();
                photoGd.setColor(Color.parseColor("#DDDDDD"));
                if ("CIRCLE".equals(shape)) {
                    photoGd.setShape(GradientDrawable.OVAL);
                } else if ("ROUNDED".equals(shape)) {
                    photoGd.setCornerRadius(cellSizePx * 0.12f);
                }
                photoIv.setBackground(photoGd);
                photoIv.setClipToOutline(true);

                if ("CIRCLE".equals(shape)) {
                    photoIv.setOutlineProvider(new ViewOutlineProvider() {
                        @Override
                        public void getOutline(View v, android.graphics.Outline o) {
                            o.setOval(0, 0, v.getWidth(), v.getHeight());
                        }
                    });
                } else if ("ROUNDED".equals(shape)) {
                    final float rad = cellSizePx * 0.12f;
                    photoIv.setOutlineProvider(new ViewOutlineProvider() {
                        @Override
                        public void getOutline(View v, android.graphics.Outline o) {
                            o.setRoundRect(0, 0, v.getWidth(), v.getHeight(), rad);
                        }
                    });
                }

                photoIv.setTag(R.id.btn_set_background, "GRID_CELL_" + cellIdx);

                // ── Mask / Frame data ────────────────────────────
                String frameMask = cellObj.optString("frameMask", "");
                String frameTop = cellObj.optString("frameTop", "");
                boolean hasCellMask = !frameMask.isEmpty() || !frameTop.isEmpty();


                // ── Photo data ───────────────────────────────────
                final String finalFrameMask = cellObj.optString("frameMask", "");
                final String finalFrameTop = cellObj.optString("frameTop", "");
                final boolean finalHasMask = !finalFrameMask.isEmpty() || !finalFrameTop.isEmpty();
                final int finalCellSize = cellSizePx;
                final String finalShape = shape;
                final int finalTotalCells = totalCells;

// ✅ Mask tag store
                if (finalHasMask) {
                    photoIv.setTag(R.id.btn_sticker_gallery,
                            finalFrameMask + "|||" + finalFrameTop);
                }

                String photoBitmapBase64 = cellObj.optString("photoBitmap", "");
                String photoUri = cellObj.optString("photoUri", "");


                if (!photoBitmapBase64.isEmpty()) {
                    // ── Base64 bitmap ────────────────────────────────
                    try {
                        Bitmap bmp = base64ToBitmap(photoBitmapBase64);
                        if (bmp != null) {
                            if (finalHasMask && !finalFrameMask.isEmpty()) {
                                final Bitmap finalBmp = bmp;
                                Glide.with(this).asBitmap().load(finalFrameMask)
                                        .into(new CustomTarget<Bitmap>() {
                                            @Override
                                            public void onResourceReady(
                                                    @NonNull Bitmap maskBitmap,
                                                    @Nullable Transition<? super Bitmap> t) {
                                                applyFrameToCellWithPhoto(
                                                        photoIv, finalBmp,
                                                        maskBitmap, finalFrameTop,
                                                        Color.TRANSPARENT, finalCellSize);
                                                // ✅ Last cell = hide loader
                                                if (cellIdx == finalTotalCells - 1)
                                                    hideGridLoader();
                                            }

                                            @Override
                                            public void onLoadCleared(@Nullable Drawable p) {
                                            }
                                        });
                            } else {
                                Bitmap clipped = clipBitmapToShape(bmp, cellSizePx, shape);
                                photoIv.setImageBitmap(clipped);
                                photoIv.setBackground(null);
                                photoIv.setClipToOutline(false);
                                if (cellIdx == finalTotalCells - 1) hideGridLoader();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else if (!photoUri.isEmpty()) {
                    // ── URI load ─────────────────────────────────────
                    Glide.with(this)
                            .asBitmap()
                            .load(Uri.parse(photoUri))
                            .into(new CustomTarget<Bitmap>(cellSizePx, cellSizePx) {
                                @Override
                                public void onResourceReady(
                                        @NonNull Bitmap bitmap,
                                        @Nullable Transition<? super Bitmap> transition) {

                                    if (finalHasMask && !finalFrameMask.isEmpty()) {
                                        Glide.with(MainActivity.this)
                                                .asBitmap()
                                                .load(finalFrameMask)
                                                .into(new CustomTarget<Bitmap>() {
                                                    @Override
                                                    public void onResourceReady(
                                                            @NonNull Bitmap maskBmp,
                                                            @Nullable Transition<? super Bitmap> t) {
                                                        applyFrameToCellWithPhoto(
                                                                photoIv, bitmap,
                                                                maskBmp, finalFrameTop,
                                                                Color.TRANSPARENT, finalCellSize);
                                                        if (cellIdx == finalTotalCells - 1)
                                                            hideGridLoader();
                                                    }

                                                    @Override
                                                    public void onLoadCleared(
                                                            @Nullable Drawable p) {
                                                    }
                                                });
                                    } else {
                                        Bitmap clipped = clipBitmapToShape(
                                                bitmap, finalCellSize, finalShape);
                                        photoIv.setImageBitmap(clipped);
                                        photoIv.setBackground(null);
                                        photoIv.setClipToOutline(false);
                                        if (cellIdx == finalTotalCells - 1)
                                            hideGridLoader();
                                    }
                                }

                                @Override
                                public void onLoadCleared(@Nullable Drawable placeholder) {
                                }
                            });

                } else if (finalHasMask) {
                    // ── No photo, mask only → empty frame ────────────
                    Glide.with(this).asBitmap().load(finalFrameMask)
                            .into(new CustomTarget<Bitmap>() {
                                @Override
                                public void onResourceReady(
                                        @NonNull Bitmap maskBitmap,
                                        @Nullable Transition<? super Bitmap> t) {
                                    applyEmptyCellFrame(
                                            photoIv, maskBitmap,
                                            finalFrameTop,
                                            Color.TRANSPARENT, finalCellSize);
                                    if (cellIdx == finalTotalCells - 1)
                                        hideGridLoader();
                                }

                                @Override
                                public void onLoadCleared(@Nullable Drawable p) {
                                }
                            });
                } else {
                    // ── No photo, no mask → placeholder ─────────────
                    if (cellIdx == finalTotalCells - 1) hideGridLoader();
                }
                // ── else: no photo, no mask → placeholder (photoGd already set)

                final ImageView finalPhoto = photoIv;

                setGridCellPhotoTouch(
                        finalPhoto,
                        gridContainer,
                        cellIdx,
                        newDataList,
                        shape,
                        cellSizePx
                );

                cell.addView(photoIv);

                // ── Name TextView ────────────────────────────────
                TextView nameTv = null;
                if (showName) {
                    nameTv = new TextView(this);
                    nameTv.setText(cellObj.optString("name", "Name " + (cellIdx + 1)));
                    nameTv.setTextSize(13);
                    int nameColor = Color.BLACK;
                    try {
                        Object nc = cellObj.get("nameColor");
                        if (nc instanceof Integer) {
                            nameColor = (Integer) nc;
                        } else if (nc instanceof Long) {
                            nameColor = ((Long) nc).intValue();
                        } else if (nc instanceof String) {
                            String s = ((String) nc).trim();
                            if (!s.isEmpty()) nameColor = Integer.parseInt(s);
                        }
                    } catch (Exception ignore) {
                    }
                    nameTv.setTextColor(nameColor);
                    nameTv.setGravity(Gravity.CENTER);
                    nameTv.setTypeface(null, Typeface.BOLD);
                    nameTv.setMaxLines(1);
                    nameTv.setEllipsize(android.text.TextUtils.TruncateAt.END);
                    nameTv.setTag(R.id.btn_ms_select_all, "NAME_" + cellIdx);
                    LinearLayout.LayoutParams nameLp = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT, nameH);
                    nameTv.setLayoutParams(nameLp);
                    cell.addView(nameTv);
                }

                // ── Info TextView ────────────────────────────────
                TextView infoTv = null;
                if (showInfo) {
                    infoTv = new TextView(this);
                    infoTv.setText(cellObj.optString("info", "0.00%"));
                    infoTv.setTextSize(12);
                    infoTv.setTextColor(Color.parseColor("#FF9800"));
                    infoTv.setGravity(Gravity.CENTER);
                    infoTv.setTypeface(null, Typeface.BOLD);
                    infoTv.setMaxLines(1);
                    infoTv.setTag(R.id.btn_ms_select_all, "INFO_" + cellIdx);
                    LinearLayout.LayoutParams infoLp = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT, infoH);
                    infoTv.setLayoutParams(infoLp);
                    cell.addView(infoTv);
                }

                final TextView finalName = nameTv;
                final TextView finalInfo = infoTv;

                if (finalName != null) {
                    finalName.setOnClickListener(v ->
                            showCellEditDialog(finalPhoto, finalName, null,
                                    cellIdx, newDataList));
                }
                if (finalInfo != null) {
                    finalInfo.setOnClickListener(v ->
                            showCellEditDialog(finalPhoto, null, finalInfo,
                                    cellIdx, newDataList));
                }

                gridContainer.addView(cell);
            }
        }

        gridContainer.setTag(R.id.btn_set_background, "GRID_FRAME");
        gridContainer.setTag(R.id.btn_grid_frame, new GridMeta(
                rows, cols, shape, cellSizePx,
                showName, showInfo, newDataList
        ));

        applyTouchListenerForGrid(gridContainer);
        attachGridEditOpenListener(gridContainer);

        exportToJson();

        Toast.makeText(this,
                "✅ Grid update થઈ ગઈ: " + rows + "×" + cols,
                Toast.LENGTH_SHORT).show();
    }


    private void attachGridEditOpenListener(RelativeLayout gridContainer) {

        gridContainer.setLongClickable(true);

        gridContainer.setOnLongClickListener(v -> {
            Toast.makeText(this, "Grid long click", Toast.LENGTH_SHORT).show();

            currentlySelectedGrid = gridContainer;
            selectView(gridContainer);
            showGridEditPopup(gridContainer);
            return true;
        });

        for (int i = 0; i < gridContainer.getChildCount(); i++) {
            View cell = gridContainer.getChildAt(i);

            cell.setLongClickable(true);

            cell.setOnLongClickListener(v -> {
                Toast.makeText(this, "Cell long click", Toast.LENGTH_SHORT).show();

                currentlySelectedGrid = gridContainer;
                selectView(gridContainer);
                showGridEditPopup(gridContainer);
                return true;
            });

            if (cell instanceof ViewGroup) {
                ViewGroup vg = (ViewGroup) cell;

                for (int j = 0; j < vg.getChildCount(); j++) {
                    View child = vg.getChildAt(j);
                    child.setLongClickable(true);

                    child.setOnLongClickListener(v -> {
                        Toast.makeText(this, "Grid child long click", Toast.LENGTH_SHORT).show();

                        currentlySelectedGrid = gridContainer;
                        selectView(gridContainer);
                        showGridEditPopup(gridContainer);
                        return true;
                    });
                }
            }
        }
    }


    private void showCellEditDialog(
            ImageView photoIv,
            TextView nameTv,
            TextView infoTv,
            int cellIdx,
            List<JSONObject> cellDataList) {

        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setTitle("Cell " + (cellIdx + 1) + " Edit");

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(24, 16, 24, 16);

        // ── Photo button
        Button btnPhoto = new Button(this);
        btnPhoto.setText("📷 Photo Select / Change");
        btnPhoto.setTextColor(Color.WHITE);
        btnPhoto.setBackgroundColor(Color.parseColor("#1565C0"));
        root.addView(btnPhoto);

        // ── Name
        android.widget.EditText etName = new android.widget.EditText(this);
        if (nameTv != null) {
            TextView lblName = new TextView(this);
            lblName.setText("Name:");
            lblName.setPadding(0, 12, 0, 4);
            root.addView(lblName);
            etName.setText(nameTv.getText());
            root.addView(etName);
        }

        // ── Info
        android.widget.EditText etInfo = new android.widget.EditText(this);
        if (infoTv != null) {
            TextView lblInfo = new TextView(this);
            lblInfo.setText("Info (%):");
            lblInfo.setPadding(0, 12, 0, 4);
            root.addView(lblInfo);
            etInfo.setText(infoTv.getText());
            root.addView(etInfo);
        }

        // ── Name color
        if (nameTv != null) {
            Button btnNameColor = new Button(this);
            btnNameColor.setText("Name Color");
            btnNameColor.setPadding(0, 8, 0, 0);
            root.addView(btnNameColor);
            final TextView finalNameTv = nameTv;
            btnNameColor.setOnClickListener(v ->
                    showColorPickerPopup(
                        finalNameTv.getCurrentTextColor(),
                        color -> {
                            finalNameTv.setTextColor(color);
                                    exportToJson();
                        }));
        }

        // ── Info color
        if (infoTv != null) {
            Button btnInfoColor = new Button(this);
            btnInfoColor.setText("Info Color");
            root.addView(btnInfoColor);
            final TextView finalInfoTv = infoTv;
            btnInfoColor.setOnClickListener(v ->
                    showColorPickerPopup(
                        finalInfoTv.getCurrentTextColor(),
                        color -> {
                            finalInfoTv.setTextColor(color);
                                    exportToJson();
                        }));
        }

        // ── Remove photo
        Button btnRemove = new Button(this);
        btnRemove.setText("✕ Photo Remove");
        btnRemove.setTextColor(Color.WHITE);
        btnRemove.setBackgroundColor(Color.parseColor("#C62828"));
        LinearLayout.LayoutParams removeLp =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
        removeLp.setMargins(0, 8, 0, 0);
        btnRemove.setLayoutParams(removeLp);
        root.addView(btnRemove);

        b.setView(root);
        final AlertDialog[] dialogRef = {null};

        // ── Photo select
        btnPhoto.setOnClickListener(v -> {
            pendingGridCellTarget = photoIv;
            pendingGridCellIdx = cellIdx;
            pendingGridCellDataList = cellDataList;
            if (dialogRef[0] != null) dialogRef[0].dismiss();

            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(
                    Intent.createChooser(intent, "Photo Select"),
                    REQUEST_GRID_CELL_PHOTO);
        });

        // ── Remove photo
        btnRemove.setOnClickListener(v -> {
            GradientDrawable gd = new GradientDrawable();
            gd.setColor(Color.parseColor("#DDDDDD"));
            if ("CIRCLE".equals(pendingGridShape))
                gd.setShape(GradientDrawable.OVAL);
            else if ("ROUNDED".equals(pendingGridShape))
                gd.setCornerRadius(pendingGridCellSize * 0.12f);
            photoIv.setImageDrawable(null);
            photoIv.setBackground(gd);
            try {
                if (cellDataList != null)
                    cellDataList.get(cellIdx).put("photoUri", "");
            } catch (Exception e) {
                e.printStackTrace();
            }
            exportToJson();
        });

        b.setPositiveButton("Apply", (d, w) -> {
            // ── Name apply
            if (nameTv != null) {
                String name = etName.getText().toString();
                nameTv.setText(name);
                try {
                    if (cellDataList != null)
                        cellDataList.get(cellIdx).put("name", name);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            // ── Info apply
            if (infoTv != null) {
                String info = etInfo.getText().toString();
                infoTv.setText(info);
                try {
                    if (cellDataList != null)
                        cellDataList.get(cellIdx).put("info", info);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            exportToJson();
        });

        b.setNegativeButton("Cancel", null);
        dialogRef[0] = b.show();
    }


    private void showPhotoFrameDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Photo Frame");

        LinearLayout mainContainer = new LinearLayout(this);
        mainContainer.setOrientation(LinearLayout.VERTICAL);
        mainContainer.setPadding(16, 16, 16, 16);

        // ── Step Label
        TextView lblStep = new TextView(this);
        lblStep.setText("Step 1: Frame પસંદ કરો");
        lblStep.setTextSize(14);
        lblStep.setTextColor(Color.parseColor("#1A237E"));
        lblStep.setPadding(0, 0, 0, 8);
        mainContainer.addView(lblStep);

        // ── Selected Frame Preview
        final ImageView selectedFramePreview = new ImageView(this);
        selectedFramePreview.setScaleType(ImageView.ScaleType.FIT_CENTER);
        selectedFramePreview.setBackgroundColor(Color.parseColor("#F5F5F5"));
        LinearLayout.LayoutParams fpP = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 200);
        fpP.setMargins(0, 0, 0, 8);
        selectedFramePreview.setLayoutParams(fpP);
        selectedFramePreview.setImageResource(android.R.drawable.ic_menu_gallery);
        mainContainer.addView(selectedFramePreview);

        // ── Selected Frame URL store
        final String[] selectedFrame = {""};

        // ── Color Preview Box
        final View colorPreviewBox = new View(this);
        colorPreviewBox.setBackgroundColor(Color.parseColor("#F5F5F5"));
        LinearLayout.LayoutParams cpP = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 40);
        cpP.setMargins(0, 0, 0, 4);
        colorPreviewBox.setLayoutParams(cpP);
        mainContainer.addView(colorPreviewBox);

        // ── Color Label
        final TextView lblColor = new TextView(this);
        lblColor.setText("Frame Color (Optional):");
        lblColor.setTextSize(13);
        lblColor.setTextColor(Color.parseColor("#1A237E"));
        lblColor.setPadding(0, 0, 0, 4);
        mainContainer.addView(lblColor);

        // ── Quick Color Scroll
        android.widget.HorizontalScrollView colorScroll = new android.widget.HorizontalScrollView(this);
        LinearLayout.LayoutParams csP = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        csP.setMargins(0, 0, 0, 8);
        colorScroll.setLayoutParams(csP);

        LinearLayout colorBtnRow = new LinearLayout(this);
        colorBtnRow.setOrientation(LinearLayout.HORIZONTAL);
        colorBtnRow.setPadding(4, 4, 4, 4);

        String[] colorNames = {"None", "Red", "Green", "Blue", "Yellow", "Orange", "Pink", "Purple", "White", "Black"};
        int[] colors = {Color.TRANSPARENT, Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW, Color.rgb(255, 165, 0), Color.rgb(255, 105, 180), Color.rgb(128, 0, 128), Color.WHITE, Color.BLACK};

        // Reset color
        pendingFrameOverlayColor = Color.TRANSPARENT;

        for (int i = 0; i < colors.length; i++) {
            final int c = colors[i];
            final String name = colorNames[i];

            TextView colorBtn = new TextView(this);
            colorBtn.setGravity(Gravity.CENTER);
            colorBtn.setTextSize(9);
            colorBtn.setPadding(4, 4, 4, 4);

            GradientDrawable gd = new GradientDrawable();
            if (c == Color.TRANSPARENT) {
                gd.setColor(Color.WHITE);
                colorBtn.setText("✕");
                colorBtn.setTextColor(Color.RED);
            } else {
                gd.setColor(c);
                colorBtn.setText(name);
                colorBtn.setTextColor(c == Color.WHITE || c == Color.YELLOW ? Color.BLACK : Color.WHITE);
            }
            gd.setStroke(2, Color.GRAY);
            gd.setCornerRadius(6f);
            colorBtn.setBackground(gd);

            LinearLayout.LayoutParams cbP = new LinearLayout.LayoutParams(70, 70);
            cbP.setMargins(4, 0, 4, 0);
            colorBtn.setLayoutParams(cbP);

            colorBtn.setOnClickListener(v -> {
                pendingFrameOverlayColor = c;
                colorPreviewBox.setBackgroundColor(c == Color.TRANSPARENT ? Color.parseColor("#F5F5F5") : c);
                lblColor.setText("Frame Color: " + name);
            });

            colorBtnRow.addView(colorBtn);
        }

        // ── Custom Color Button
        TextView btnCustom = new TextView(this);
        btnCustom.setText("🎨");
        btnCustom.setTextSize(18);
        btnCustom.setGravity(Gravity.CENTER);
        GradientDrawable customGd = new GradientDrawable();
        customGd.setColor(Color.parseColor("#E8EAF6"));
        customGd.setStroke(2, Color.parseColor("#3F51B5"));
        customGd.setCornerRadius(6f);
        btnCustom.setBackground(customGd);
        LinearLayout.LayoutParams cP = new LinearLayout.LayoutParams(70, 70);
        cP.setMargins(4, 0, 4, 0);
        btnCustom.setLayoutParams(cP);
        btnCustom.setOnClickListener(v -> showColorPickerPopup(
                        pendingFrameOverlayColor == Color.TRANSPARENT ? Color.RED : pendingFrameOverlayColor,
                        color -> {
                            pendingFrameOverlayColor = color;
                colorPreviewBox.setBackgroundColor(color);
                lblColor.setText("Frame Color: Custom");
                        }));
        colorBtnRow.addView(btnCustom);

        colorScroll.addView(colorBtnRow);
        mainContainer.addView(colorScroll);

        // ── Add Frame Button — initially disabled
        Button btnAddFrame = new Button(this);
        btnAddFrame.setText("✅ Frame Add કરો");
        btnAddFrame.setBackgroundColor(Color.GRAY);
        btnAddFrame.setTextColor(Color.WHITE);
        btnAddFrame.setTextSize(13);
        btnAddFrame.setEnabled(false);
        LinearLayout.LayoutParams afP = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        afP.setMargins(0, 0, 0, 12);
        btnAddFrame.setLayoutParams(afP);
        mainContainer.addView(btnAddFrame);

        // ── Progress
        ProgressBar progressBar = new ProgressBar(this);
        progressBar.setVisibility(View.VISIBLE);
        mainContainer.addView(progressBar);

        // ── Frame RecyclerView
        RecyclerView frameRV = new RecyclerView(this);
        LinearLayout.LayoutParams rvP = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 500);
        frameRV.setLayoutParams(rvP);
        mainContainer.addView(frameRV);

        ScrollView scrollView = new ScrollView(this);
        scrollView.addView(mainContainer);

        // ✅ Class variable માં store
        framePickerDialog = builder.setView(scrollView).setNegativeButton("Cancel", null).create();

        // ── Frame Load
        loadFramesDirectly(frameRV, progressBar);

        // ── Frame Click
        frameRV.addOnItemTouchListener(new invite_AppConstants.RecyclerTouchListener(this, frameRV, new invite_AppConstants.RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                if (invite_photo_frame.frame_arrayList == null || invite_photo_frame.frame_arrayList.isEmpty())
                    return;

                String frameUrl = invite_photo_frame.frame_arrayList.get(position).getImageBig();
                String mask_image = invite_photo_frame.frame_arrayList.get(position).getcard_background();
                String image_top = invite_photo_frame.frame_arrayList.get(position).getemail_icon();

                // ✅ Store
                selectedFrame[0] = frameUrl;
                pendingFrameUrl = frameUrl;
                pendingMaskUrl = mask_image;
                pendingImageTopUrl = image_top;

                // ✅ Preview show
                Glide.with(MainActivity.this).load(frameUrl).into(selectedFramePreview);

                // ✅ Button enable
                btnAddFrame.setEnabled(true);
                btnAddFrame.setBackgroundColor(Color.parseColor("#1565C0"));
                lblStep.setText("✅ Frame selected! Add કરો");

                Toast.makeText(MainActivity.this, "Frame select થઈ! Add button દબાવો", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLongClick(View view, int position) {
            }
        }));

        // ── Add Frame Button Click — directly add without photo
        btnAddFrame.setOnClickListener(v -> {
            if (selectedFrame[0].isEmpty()) {
                Toast.makeText(this, "પહેલા Frame select કરો", Toast.LENGTH_SHORT).show();
                return;
            }

            // ✅ Dialog dismiss
            if (framePickerDialog != null && framePickerDialog.isShowing()) {
                framePickerDialog.dismiss();
                framePickerDialog = null;
            }

            // ✅ Photo વગર frame directly add
            addFrameWithoutPhoto(pendingFrameUrl, pendingMaskUrl, pendingImageTopUrl, pendingFrameOverlayColor);
        });

        framePickerDialog.show();
    }

    private void setGridCellPhotoTouch(
            ImageView photoIv,
            RelativeLayout gridContainer,
            int cellIdx,
            List<JSONObject> cellDataList,
            String shape,
            int cellSizePx
    ) {
        final float[] downX = {0};
        final float[] downY = {0};
        final float[] startGridX = {0};
        final float[] startGridY = {0};
        final boolean[] isMoving = {false};

        photoIv.setOnTouchListener((v, event) -> {
            switch (event.getActionMasked()) {

                case MotionEvent.ACTION_DOWN:
                    downX[0] = event.getRawX();
                    downY[0] = event.getRawY();

                    startGridX[0] = gridContainer.getX();
                    startGridY[0] = gridContainer.getY();

                    isMoving[0] = false;

                    currentlySelectedGrid = gridContainer;
                    selectView(gridContainer);

                    selectedGridPhotoForPopup = photoIv;
                    selectedGridCellIndexForPopup = cellIdx;
                    selectedGridDataListForPopup = cellDataList;
                    selectedGridShapeForPopup = shape;
                    selectedGridCellSizeForPopup = cellSizePx;

                    return true;

                case MotionEvent.ACTION_MOVE:
                    float dx = event.getRawX() - downX[0];
                    float dy = event.getRawY() - downY[0];

                    float moveDist = (float) Math.sqrt(dx * dx + dy * dy);

                    // ✅ 25px પછી જ drag ગણવું
                    if (moveDist > 25) {
                        isMoving[0] = true;

                        float newX = startGridX[0] + dx;
                        float newY = startGridY[0] + dy;

                        float minX = main_image_view.getX();
                        float minY = main_image_view.getY();
                        float maxX = main_image_view.getX() + main_image_view.getWidth() - gridContainer.getWidth();
                        float maxY = main_image_view.getY() + main_image_view.getHeight() - gridContainer.getHeight();

                        gridContainer.setX(Math.max(minX, Math.min(newX, maxX)));
                        gridContainer.setY(Math.max(minY, Math.min(newY, maxY)));

                        if (gridEditPopup != null && gridEditPopup.isShowing()) {
                            gridEditPopup.dismiss();
                        }
                    }

                    return true;

                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    float upDist = (float) Math.sqrt(
                            Math.pow(event.getRawX() - downX[0], 2) +
                                    Math.pow(event.getRawY() - downY[0], 2));

                    if (!isMoving[0] && upDist < 15) {

                        // ✅ Swap mode check
                        if (isGridSwapMode) {
                            handleGridCellSwap(
                                    photoIv, cellIdx,
                                    cellDataList, shape,
                                    cellSizePx, gridContainer);
                        } else {
                            gridContainer.postDelayed(
                                    () -> showGridEditPopup(gridContainer), 80);
                        }

                    } else if (isMoving[0]) {
                        exportToJson();
                    }
                    return true;
            }

            return true;
        });
    }


    private void handleGridCellSwap(
            ImageView tappedPhoto,
            int tappedIdx,
            List<JSONObject> dataList,
            String shape,
            int cellSizePx,
            RelativeLayout gridContainer) {

        if (selectedSwapCell == null) {
            // ── First cell select
            selectedSwapCell = tappedPhoto;
            selectedSwapCellIdx = tappedIdx;
            selectedSwapDataList = dataList;
            selectedSwapGrid = gridContainer;
            selectedSwapShape = shape;
            selectedSwapCellSize = cellSizePx;

            // ✅ Highlight selected cell
            GradientDrawable selGd = new GradientDrawable();
            selGd.setColor(Color.parseColor("#33FF9800"));
            selGd.setStroke(6, Color.parseColor("#FF9800"));
            if ("CIRCLE".equals(shape)) {
                selGd.setShape(GradientDrawable.OVAL);
            } else if ("ROUNDED".equals(shape)) {
                selGd.setCornerRadius(cellSizePx * 0.12f);
            }
            tappedPhoto.setBackground(selGd);

            Toast.makeText(this,
                    "Cell " + (tappedIdx + 1) + " selected — બીજી cell tap કરો",
                    Toast.LENGTH_SHORT).show();

        } else {
            // ── Second cell — swap!

            if (selectedSwapCell == tappedPhoto) {
                // Same cell tap = deselect
                resetSwapSelection();
                Toast.makeText(this,
                        "Selection cancel",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            // ── Data swap
            JSONObject cellA = null;
            JSONObject cellB = null;

            try {
                if (selectedSwapDataList != null
                        && selectedSwapCellIdx < selectedSwapDataList.size()) {
                    cellA = selectedSwapDataList.get(selectedSwapCellIdx);
                }
                if (dataList != null
                        && tappedIdx < dataList.size()) {
                    cellB = dataList.get(tappedIdx);
                }

                if (cellA == null || cellB == null) {
                    Toast.makeText(this,
                            "Cell data મળ્યો નહીં",
                            Toast.LENGTH_SHORT).show();
                    resetSwapSelection();
                    return;
                }

                // ── Swap all fields
                String photoUriA = cellA.optString("photoUri", "");
                String photoBitmapA = cellA.optString("photoBitmap", "");
                String nameA = cellA.optString("name", "");
                String infoA = cellA.optString("info", "");

                String photoUriB = cellB.optString("photoUri", "");
                String photoBitmapB = cellB.optString("photoBitmap", "");
                String nameB = cellB.optString("name", "");
                String infoB = cellB.optString("info", "");

                cellA.put("photoUri", photoUriB);
                cellA.put("name", nameB);
                cellA.put("info", infoB);
                if (!photoBitmapB.isEmpty()) {
                    cellA.put("photoBitmap", photoBitmapB);
                } else {
                    cellA.remove("photoBitmap");
                }

                cellB.put("photoUri", photoUriA);
                cellB.put("name", nameA);
                cellB.put("info", infoA);
                if (!photoBitmapA.isEmpty()) {
                    cellB.put("photoBitmap", photoBitmapA);
                } else {
                    cellB.remove("photoBitmap");
                }

            } catch (Exception e) {
                e.printStackTrace();
                resetSwapSelection();
                return;
            }

            // ── UI swap — ImageView drawables exchange
            android.graphics.drawable.Drawable drawA =
                    selectedSwapCell.getDrawable();
            android.graphics.drawable.Drawable drawB =
                    tappedPhoto.getDrawable();

            selectedSwapCell.setImageDrawable(drawB);
            tappedPhoto.setImageDrawable(drawA);

            // ── Placeholder restore if empty
            GradientDrawable emptyGd = new GradientDrawable();
            emptyGd.setColor(Color.parseColor("#DDDDDD"));
            if ("CIRCLE".equals(shape)) {
                emptyGd.setShape(GradientDrawable.OVAL);
            } else if ("ROUNDED".equals(shape)) {
                emptyGd.setCornerRadius(cellSizePx * 0.12f);
            }

            if (drawB == null) {
                selectedSwapCell.setBackground(emptyGd);
            } else {
                selectedSwapCell.setBackground(null);
            }

            if (drawA == null) {
                tappedPhoto.setBackground(emptyGd);
            } else {
                tappedPhoto.setBackground(null);
            }

            // ── Name/Info TextView swap in UI
            swapCellTextViews(
                    selectedSwapGrid,
                    selectedSwapCellIdx,
                    gridContainer,
                    tappedIdx,
                    selectedSwapDataList,
                    dataList);

            resetSwapSelection();
            exportToJson();

            Toast.makeText(this,
                    "✅ Cells swap થઈ ગઈ!",
                    Toast.LENGTH_SHORT).show();
        }
    }


    private void swapCellTextViews(
            RelativeLayout gridA, int idxA,
            RelativeLayout gridB, int idxB,
            List<JSONObject> dataA,
            List<JSONObject> dataB) {

        // ── Cell A views
        if (idxA < gridA.getChildCount()) {
            View cellViewA = gridA.getChildAt(idxA);
            if (cellViewA instanceof LinearLayout) {
                LinearLayout llA = (LinearLayout) cellViewA;
                for (int k = 0; k < llA.getChildCount(); k++) {
                    View child = llA.getChildAt(k);
                    Object msTag = child.getTag(R.id.btn_ms_select_all);
                    if (msTag == null) continue;
                    String ms = msTag.toString();

                    if (child instanceof TextView) {
                        TextView tv = (TextView) child;
                        if (ms.startsWith("NAME_") && dataA != null
                                && idxA < dataA.size()) {
                            tv.setText(dataA.get(idxA)
                                    .optString("name", ""));
                        } else if (ms.startsWith("INFO_")
                                && dataA != null
                                && idxA < dataA.size()) {
                            tv.setText(dataA.get(idxA)
                                    .optString("info", ""));
                        }
                    }
                }
            }
        }

        // ── Cell B views
        if (idxB < gridB.getChildCount()) {
            View cellViewB = gridB.getChildAt(idxB);
            if (cellViewB instanceof LinearLayout) {
                LinearLayout llB = (LinearLayout) cellViewB;
                for (int k = 0; k < llB.getChildCount(); k++) {
                    View child = llB.getChildAt(k);
                    Object msTag = child.getTag(R.id.btn_ms_select_all);
                    if (msTag == null) continue;
                    String ms = msTag.toString();

                    if (child instanceof TextView) {
                        TextView tv = (TextView) child;
                        if (ms.startsWith("NAME_") && dataB != null
                                && idxB < dataB.size()) {
                            tv.setText(dataB.get(idxB)
                                    .optString("name", ""));
                        } else if (ms.startsWith("INFO_")
                                && dataB != null
                                && idxB < dataB.size()) {
                            tv.setText(dataB.get(idxB)
                                    .optString("info", ""));
                        }
                    }
                }
            }
        }
    }


    private void resetSwapSelection() {
        // ── Previous selected cell highlight remove
        if (selectedSwapCell != null) {
            GradientDrawable restoreGd = new GradientDrawable();
            restoreGd.setColor(Color.TRANSPARENT);
            selectedSwapCell.setBackground(null);
        }

        selectedSwapCell = null;
        selectedSwapCellIdx = -1;
        selectedSwapDataList = null;
        selectedSwapGrid = null;
    }


    private void addFrameWithoutPhoto(String frameUrl, String maskUrl, String topUrl, int overlayColor) {

        Toast.makeText(this, "Frame add થઈ રહ્યો છે...", Toast.LENGTH_SHORT).show();

        // ── Mask load — size get
        Glide.with(this).asBitmap().load(maskUrl.isEmpty() ? topUrl : maskUrl).into(new CustomTarget<Bitmap>() {
            @Override
            public void onResourceReady(@NonNull Bitmap maskBitmap, @Nullable com.bumptech.glide.request.transition.Transition<? super Bitmap> transition) {

                int w = maskBitmap.getWidth();
                int h = maskBitmap.getHeight();

                // ── Placeholder — gray background
                Bitmap placeholder = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
                Canvas phCanvas = new Canvas(placeholder);
                phCanvas.drawColor(Color.parseColor("#CCCCCC"));

                // "Photo અહીં" text
                Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
                textPaint.setColor(Color.WHITE);
                textPaint.setTextSize(w * 0.08f);
                textPaint.setTextAlign(Paint.Align.CENTER);
                phCanvas.drawText("📷 Photo Select", w / 2f, h / 2f, textPaint);

                // ── top image load
                Glide.with(MainActivity.this).asBitmap().load(topUrl).into(new CustomTarget<Bitmap>(w, h) {
                    @Override
                    public void onResourceReady(@NonNull Bitmap topBitmap, @Nullable com.bumptech.glide.request.transition.Transition<? super Bitmap> transition) {

                        // ── Merge: placeholder + mask + top
                        MergeResult result = mergeThreeLayers(placeholder, maskBitmap, topBitmap, w, h);

                        // ✅ Add to layout
                        addMergedFrameToLayout(result.finalBitmap, topUrl, result.userMaskedBitmap);

                        placeholder.recycle();
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable p) {
                    }

                    @Override
                    public void onLoadFailed(@Nullable Drawable e) {
                        // top વગર add
                        MergeResult result = mergeThreeLayers(placeholder, maskBitmap, null, w, h);
                        addMergedFrameToLayout(result.finalBitmap, maskUrl, result.userMaskedBitmap);
                        placeholder.recycle();
                    }
                });
            }

            @Override
            public void onLoadCleared(@Nullable Drawable p) {
            }

            @Override
            public void onLoadFailed(@Nullable Drawable e) {
                Toast.makeText(MainActivity.this, "Frame load failed", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void mergeImageWithFrame(Uri croppedUri, String maskUrl, String imageTopUrl) {
        Toast.makeText(this, "Frame apply થઈ રહ્યો છે...", Toast.LENGTH_SHORT).show();

        File croppedFile = new File(croppedUri.getPath());

        Glide.with(this).asBitmap().load(maskUrl).into(new CustomTarget<Bitmap>() {
            @Override
            public void onResourceReady(@NonNull Bitmap maskBitmap, @Nullable com.bumptech.glide.request.transition.Transition<? super Bitmap> transition) {

                int w = maskBitmap.getWidth();
                int h = maskBitmap.getHeight();

                Glide.with(MainActivity.this).asBitmap().load(croppedFile).into(new CustomTarget<Bitmap>(w, h) {
                    @Override
                    public void onResourceReady(@NonNull Bitmap userBitmap, @Nullable com.bumptech.glide.request.transition.Transition<? super Bitmap> transition) {

                        Glide.with(MainActivity.this).asBitmap().load(imageTopUrl).into(new CustomTarget<Bitmap>(w, h) {
                            @Override
                            public void onResourceReady(@NonNull Bitmap topBitmap, @Nullable com.bumptech.glide.request.transition.Transition<? super Bitmap> transition) {

                                // ✅ MergeResult — finalBitmap + userMaskedBitmap
                                MergeResult mergeResult = mergeThreeLayers(userBitmap, maskBitmap, topBitmap, w, h);

                                // ✅ 3 parameters pass
                                addMergedFrameToLayout(mergeResult.finalBitmap, imageTopUrl, mergeResult.userMaskedBitmap);
                            }

                            @Override
                            public void onLoadCleared(@Nullable Drawable p) {
                            }

                            @Override
                            public void onLoadFailed(@Nullable Drawable e) {
                                // image_top વગર merge
                                MergeResult mergeResult = mergeThreeLayers(userBitmap, maskBitmap, null, w, h);

                                // ✅ 3 parameters pass
                                addMergedFrameToLayout(mergeResult.finalBitmap, maskUrl, mergeResult.userMaskedBitmap);
                            }
                        });
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable p) {
                    }

                    @Override
                    public void onLoadFailed(@Nullable Drawable e) {
                        try {
                            Bitmap userBmp = BitmapFactory.decodeFile(croppedFile.getAbsolutePath());
                            if (userBmp != null) {
                                MergeResult mergeResult = mergeThreeLayers(userBmp, maskBitmap, null, w, h);

                                // ✅ 3 parameters pass
                                addMergedFrameToLayout(mergeResult.finalBitmap, maskUrl, mergeResult.userMaskedBitmap);
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                });
            }

            @Override
            public void onLoadCleared(@Nullable Drawable p) {
            }

            @Override
            public void onLoadFailed(@Nullable Drawable e) {
                Toast.makeText(MainActivity.this, "Mask image load failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private MergeResult mergeThreeLayers(Bitmap userImage, Bitmap maskImage, @Nullable Bitmap topImage, int width, int height) {

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);

        Bitmap userScaled = Bitmap.createScaledBitmap(userImage, width, height, true);
        Bitmap maskScaled = Bitmap.createScaledBitmap(maskImage, width, height, true);

        boolean maskHasTransparency = hasTransparentPixels(maskScaled);

        Bitmap userMasked = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas maskCanvas = new Canvas(userMasked);

        if (maskHasTransparency) {
            maskCanvas.drawBitmap(maskScaled, 0, 0, paint);
            Paint srcIn = new Paint(Paint.ANTI_ALIAS_FLAG);
            srcIn.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            maskCanvas.drawBitmap(userScaled, 0, 0, srcIn);
            srcIn.setXfermode(null);
        } else {
            maskCanvas.drawBitmap(userScaled, 0, 0, paint);
            Paint dstIn = new Paint(Paint.ANTI_ALIAS_FLAG);
            dstIn.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
            maskCanvas.drawBitmap(maskScaled, 0, 0, dstIn);
            dstIn.setXfermode(null);
        }

        // ✅ userMasked copy — tag store માટે
        Bitmap userMaskedCopy = userMasked.copy(Bitmap.Config.ARGB_8888, false);

        // ── Final result
        Bitmap result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        canvas.drawBitmap(userMasked, 0, 0, paint);

        if (topImage != null) {
            Bitmap topScaled = Bitmap.createScaledBitmap(topImage, width, height, true);
            if (pendingFrameOverlayColor != Color.TRANSPARENT) {
                Bitmap tinted = applyColorTint(topScaled, pendingFrameOverlayColor);
                canvas.drawBitmap(tinted, 0, 0, paint);
                tinted.recycle();
            } else {
                canvas.drawBitmap(topScaled, 0, 0, paint);
            }
            topScaled.recycle();
        }

        userScaled.recycle();
        maskScaled.recycle();
        userMasked.recycle();

        // ✅ MergeResult return
        return new MergeResult(result, userMaskedCopy);
    }


    private Bitmap applyColorTint(Bitmap src, int tintColor) {
        Bitmap tinted = Bitmap.createBitmap(src.getWidth(), src.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(tinted);

        // Original image draw
        canvas.drawBitmap(src, 0, 0, null);

        // Color overlay — alpha 120 (semi-transparent)
        Paint tintPaint = new Paint();
        tintPaint.setColor(tintColor);
        tintPaint.setAlpha(150); // 0-255 (150 = ~60% opacity)
        tintPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP));
        canvas.drawRect(0, 0, src.getWidth(), src.getHeight(), tintPaint);
        tintPaint.setXfermode(null);

        return tinted;
    }


    // ── Mask PNG transparent pixels check
    private boolean hasTransparentPixels(Bitmap bmp) {
        // Sample center pixels check — fast
        int w = bmp.getWidth();
        int h = bmp.getHeight();

        // 4 corners + center check
        int[] checkX = {0, w - 1, w / 2, w / 4, 3 * w / 4};
        int[] checkY = {0, h - 1, h / 2, h / 4, 3 * h / 4};

        for (int i = 0; i < checkX.length; i++) {
            int pixel = bmp.getPixel(checkX[i], checkY[i]);
            if (Color.alpha(pixel) < 255) {
                return true; // transparent pixels found
            }
        }
        return false;
    }


    private void scrollToCenter(View view) {
        HorizontalScrollView scrollView = findViewById(R.id.bottom_bt_scroll);

        // post() નો ઉપયોગ કરવાથી બટનનું ચોક્કસ લોકેશન મળશે
        scrollView.post(() -> {
            int screenWidth = getResources().getDisplayMetrics().widthPixels;

            // બટનની લેફ્ટ પોઝિશન અને તેની પહોળાઈ
            int viewLeft = view.getLeft();
            int viewWidth = view.getWidth();

            // ગણતરી: (બટનની લેફ્ટ પોઝિશન) - (સ્ક્રીનનું સેન્ટર) + (બટનનું અડધું માપ)
            int scrollX = viewLeft - (screenWidth / 2) + (viewWidth / 2);

            // એનિમેશન સાથે સેન્ટરમાં લાવવા માટે
            scrollView.smoothScrollTo(scrollX, 0);
        });
    }


    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            if (currentlySelectedView instanceof ImageView && currentlySelectedView != main_image_view) {
                ImageView view = (ImageView) currentlySelectedView;

                // સ્કેલ ફેક્ટર ગણો
                scaleFactor *= detector.getScaleFactor();

                // સાઈઝ બહુ નાની કે બહુ મોટી ન થઈ જાય તેની મર્યાદા (0.5x થી 5.0x)
                scaleFactor = Math.max(0.5f, Math.min(scaleFactor, 5.0f));

                // ઈમેજની સાઈઝ બદલો
                view.setScaleX(scaleFactor);
                view.setScaleY(scaleFactor);

                return true;
            }
            return false;
        }
    }

    private int dp(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density + 0.5f);
    }

    private void showGridLoader(String message) {
        runOnUiThread(() -> {
            if (gridLoaderOverlay != null) return;

            android.widget.FrameLayout overlay = new android.widget.FrameLayout(this);
            overlay.setBackgroundColor(Color.parseColor("#CC000000"));

            android.widget.LinearLayout box = new android.widget.LinearLayout(this);
            box.setOrientation(android.widget.LinearLayout.VERTICAL);
            box.setGravity(android.view.Gravity.CENTER);
            box.setPadding(dp(32), dp(32), dp(32), dp(32));

            android.graphics.drawable.GradientDrawable boxBg =
                    new android.graphics.drawable.GradientDrawable();
            boxBg.setColor(Color.WHITE);
            boxBg.setCornerRadius(dp(16));
            box.setBackground(boxBg);

            android.widget.ProgressBar spinner = new android.widget.ProgressBar(this);
            android.widget.LinearLayout.LayoutParams spinLp =
                    new android.widget.LinearLayout.LayoutParams(dp(56), dp(56));
            spinLp.setMargins(0, 0, 0, dp(16));
            spinner.setLayoutParams(spinLp);
            box.addView(spinner);

            android.widget.TextView tvMsg = new android.widget.TextView(this);
            tvMsg.setText(message);
            tvMsg.setTextSize(14);
            tvMsg.setTextColor(Color.parseColor("#212121"));
            tvMsg.setGravity(android.view.Gravity.CENTER);
            tvMsg.setTypeface(null, android.graphics.Typeface.BOLD);
            box.addView(tvMsg);

            android.widget.TextView tvSub = new android.widget.TextView(this);
            tvSub.setText("કૃપા કરી રાહ જુઓ...");
            tvSub.setTextSize(12);
            tvSub.setTextColor(Color.parseColor("#757575"));
            tvSub.setGravity(android.view.Gravity.CENTER);
            tvSub.setPadding(0, dp(4), 0, 0);
            box.addView(tvSub);

            android.widget.FrameLayout.LayoutParams boxLp =
                    new android.widget.FrameLayout.LayoutParams(
                            android.widget.FrameLayout.LayoutParams.WRAP_CONTENT,
                            android.widget.FrameLayout.LayoutParams.WRAP_CONTENT);
            boxLp.gravity = android.view.Gravity.CENTER;
            overlay.addView(box, boxLp);

            android.view.ViewGroup rootView =
                    (android.view.ViewGroup) mainLayout.getParent();
            if (rootView != null) {
                rootView.addView(overlay,
                        new android.view.ViewGroup.LayoutParams(
                                android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                                android.view.ViewGroup.LayoutParams.MATCH_PARENT));
                gridLoaderOverlay = overlay;
            }
        });
    }

    private void hideGridLoader() {
        runOnUiThread(() -> {
            if (gridLoaderOverlay == null) return;
            android.view.ViewGroup parent =
                    (android.view.ViewGroup) gridLoaderOverlay.getParent();
            if (parent != null) parent.removeView(gridLoaderOverlay);
            gridLoaderOverlay = null;
        });
    }

    private void launchUCropForExistingFrame(Uri sourceUri) {
        File outputDir = new File(getCacheDir(), "ucrop");
        if (!outputDir.exists()) outputDir.mkdirs();

        Uri destinationUri = Uri.fromFile(new File(outputDir, "cropped_" + System.currentTimeMillis() + ".jpg"));

        // ✅ Existing frame ની mask URL
        Object maskTag = currentFrameTargetSticker.getTag(R.id.btn_location);
        String maskUrl = maskTag != null ? maskTag.toString() : "";

        Object topTag = currentFrameTargetSticker.getTag(R.id.btn_add_sticker);
        String topUrl = topTag != null ? topTag.toString() : "";

        String ratioUrl = maskUrl.isEmpty() ? topUrl : maskUrl;

        if (ratioUrl.isEmpty()) {
            // Fallback — 1:1
            UCrop.of(sourceUri, destinationUri).withAspectRatio(1, 1).withMaxResultSize(1000, 1000).withOptions(getUCropOptions()).start(this, REQUEST_UCROP);
            return;
        }

        Glide.with(this).asBitmap().load(ratioUrl).into(new CustomTarget<Bitmap>() {
            @Override
            public void onResourceReady(@NonNull Bitmap bmp, @Nullable com.bumptech.glide.request.transition.Transition<? super Bitmap> transition) {

                UCrop.of(sourceUri, destinationUri).withAspectRatio(bmp.getWidth(), bmp.getHeight()).withMaxResultSize(1000, 1000).withOptions(getUCropOptions()).start(MainActivity.this, REQUEST_UCROP);
            }

            @Override
            public void onLoadCleared(@Nullable Drawable p) {
            }

            @Override
            public void onLoadFailed(@Nullable Drawable e) {
                UCrop.of(sourceUri, destinationUri).withAspectRatio(1, 1).withMaxResultSize(1000, 1000).withOptions(getUCropOptions()).start(MainActivity.this, REQUEST_UCROP);
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable android.content.Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_STICKER_IMAGE && resultCode == RESULT_OK && data != null) {
            android.net.Uri imageUri = data.getData();
            addNewSticker(imageUri);
        }

        if (requestCode == 105 && resultCode == RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();
            if (selectedImageUri != null) {
                removeBackgroundOffline(selectedImageUri);
            }
        }

        if (requestCode == REQUEST_VOICE_INPUT && resultCode == RESULT_OK && data != null) {
            ArrayList<String> results = data.getStringArrayListExtra(android.speech.RecognizerIntent.EXTRA_RESULTS);
            if (results != null && !results.isEmpty() && voiceTargetInput != null) {
                String voiceText = results.get(0);
                int cursor = voiceTargetInput.getSelectionStart();
                voiceTargetInput.getText().insert(cursor, voiceText);
            }
            voiceTargetInput = null;
            return;
        }

        // ── Sticker pick
        if (requestCode == PICK_STICKER_IMAGE && resultCode == RESULT_OK && data != null) {
            android.net.Uri imageUri = data.getData();
            addNewSticker(imageUri);
        }

        if (requestCode == 105 && resultCode == RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();
            if (selectedImageUri != null) {
                removeBackgroundOffline(selectedImageUri);
            }
        }

        if (requestCode == REQUEST_STICKER_PICK && resultCode == RESULT_OK && data != null) {

            // ✅ "image" key — invite_sticker_lis_common.java જે send કરે છે
            String imageUrl = data.getStringExtra("image");
            String catId = data.getStringExtra("catid");
            String name = data.getStringExtra("name");

            int resId = data.getIntExtra("SELECTED_IMAGE_RES_ID", -1);
            String filePath = data.getStringExtra("SELECTED_IMAGE_PATH");

            if (imageUrl != null && !imageUrl.isEmpty()) {
                addStickerFromUrl(imageUrl, catId); // ✅ URL image add થશે
            } else if (resId != -1) {
                addStickerFromResId(resId);
            } else if (filePath != null) {
                addNewSticker(Uri.fromFile(new File(filePath)));
            }
        }


        if (requestCode == REQUEST_FRAME_GALLERY_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri sourceUri = data.getData();
            if (sourceUri != null) {

                // ✅ Dialog dismiss (if showing)
                if (framePickerDialog != null && framePickerDialog.isShowing()) {
                    framePickerDialog.dismiss();
                    framePickerDialog = null;
                }

                if (pendingFrameUrl == null || pendingFrameUrl.isEmpty()) {
                    Toast.makeText(this, "Frame URL મળ્યો નહીં", Toast.LENGTH_SHORT).show();
                    return;
                }

                // ✅ Existing frame update vs new frame
                if (currentFrameTargetSticker != null) {
                    // ── Existing frame માં photo set
                    launchUCropForExistingFrame(sourceUri);
                } else {
                    // ── New frame
                    launchUCropWithFrame(sourceUri);
                }
            }
        }

        if (requestCode == REQUEST_UCROP && resultCode == RESULT_OK) {
            Uri croppedUri = UCrop.getOutput(data);
            if (croppedUri != null) {
                if (currentFrameTargetSticker != null) {
                    // ✅ Existing frame update
                    updateExistingFrameWithPhoto(croppedUri, currentFrameTargetSticker);
                    currentFrameTargetSticker = null;
                } else {
                    // New frame merge
                    mergeImageWithFrame(croppedUri, pendingMaskUrl, pendingImageTopUrl);
                }
            }
        }

        // ── PhotoSetActivity result
        if (requestCode == REQUEST_PHOTO_SET_ACTIVITY && resultCode == RESULT_OK && data != null) {

            String finalPath = data.getStringExtra("finalPath");
            String userMaskedPath = data.getStringExtra("userMaskedPath");

            if (finalPath != null && currentFrameTargetSticker != null) {

                // ── Final merged bitmap set
                Bitmap finalBmp = BitmapFactory.decodeFile(finalPath);
                if (finalBmp != null) {
                    currentFrameTargetSticker.setImageBitmap(finalBmp);
                }

                // ── UserMasked bitmap tag update (color change માટે)
                if (userMaskedPath != null) {
                    Bitmap userMaskedBmp = BitmapFactory.decodeFile(userMaskedPath);
                    if (userMaskedBmp != null) {
                        currentFrameTargetSticker.setTag(R.id.tv_size_label, userMaskedBmp);
                    }
                }

                exportToJson();
                Toast.makeText(this, "✅ Photo set થઈ ગયો!", Toast.LENGTH_SHORT).show();
            }

            currentFrameTargetSticker = null;
        }

// ── PhotoSetActivity cancel
        if (requestCode == REQUEST_PHOTO_SET_ACTIVITY && resultCode == RESULT_CANCELED) {
            currentFrameTargetSticker = null;
        }


        if (requestCode == REQUEST_IMAGE_CROP && resultCode == RESULT_OK && data != null) {

            String croppedPath = data.getStringExtra("croppedPath");
            boolean isPng = data.getBooleanExtra("isPng", false);

            if (croppedPath != null && pendingCropTarget != null) {

                // ✅ ARGB_8888 config — alpha maintain
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                Bitmap cropped = BitmapFactory.decodeFile(croppedPath, options);

                if (cropped != null) {
                    pendingCropTarget.setImageBitmap(cropped);

                    // ✅ URI tag — PNG extension maintain
                    pendingCropTarget.setTag(R.id.btn_set_background, "file://" + croppedPath);

                    exportToJson();
                    Toast.makeText(this, "✅ Crop apply થઈ ગઈ!", Toast.LENGTH_SHORT).show();
                }
            }
            pendingCropTarget = null;
        }


        if (requestCode == REQUEST_FREEHAND_CROP && resultCode == RESULT_OK && data != null) {

            String path = data.getStringExtra("croppedPath");
            if (path != null && pendingFreehandTarget != null) {
                BitmapFactory.Options opts = new BitmapFactory.Options();
                opts.inPreferredConfig = Bitmap.Config.ARGB_8888;
                Bitmap bmp = BitmapFactory.decodeFile(path, opts);
                if (bmp != null) {
                    pendingFreehandTarget.setImageBitmap(bmp);
                    pendingFreehandTarget.setTag(R.id.btn_set_background, "file://" + path);
                    exportToJson();
                    Toast.makeText(this, "✅ Freehand cut apply!", Toast.LENGTH_SHORT).show();
                }
            }
            pendingFreehandTarget = null;
        }


        if (requestCode == REQUEST_LOCATION_PICKER && resultCode == RESULT_OK && data != null) {

            String mapUrl = data.getStringExtra(LocationPickerActivity.RESULT_MAP_URL);
            int iconResId = data.getIntExtra(LocationPickerActivity.RESULT_ICON_RES_ID, -1);
            String imgPath = data.getStringExtra(LocationPickerActivity.RESULT_CUSTOM_IMG);

            if (mapUrl != null && !mapUrl.isEmpty()) {
                // 🔗 Link tab — URL location
                addLocationIconWithLink(mapUrl);

            } else if (iconResId != -1) {
                // 📌 Icons tab — location_1 to location_16
                addIconLocation(iconResId);

            } else if (imgPath != null && !imgPath.isEmpty()) {
                // 🖼 Gallery tab — custom image
                addCustomImageLocation(imgPath);
            }
        }

        if (requestCode == REQUEST_TEXT_BG_IMAGE1 && resultCode == RESULT_OK && data != null) {

            Uri selectedUri = data.getData();
            if (selectedUri != null && pendingTextBgTarget != null) {
                applyTextBgImage(selectedUri, pendingTextBgTarget);
            }
            pendingTextBgTarget = null;
        }

        if (requestCode == REQUEST_GRID_CELL_PHOTO &&
                resultCode == RESULT_OK && data != null) {

            Uri uri = data.getData();
            if (uri == null || pendingGridCellTarget == null) return;

            final ImageView target = pendingGridCellTarget;
            final int idx = pendingGridCellIdx;
            final List<JSONObject> dList = pendingGridCellDataList;
            final String sh = pendingGridShape;
            final int sz = pendingGridCellSize;

            Glide.with(this)
                    .asBitmap()
                    .load(uri)
                    .into(new com.bumptech.glide.request.target
                            .CustomTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(
                                @NonNull Bitmap bitmap,
                                @Nullable com.bumptech.glide.request.transition
                                        .Transition<? super Bitmap> t) {

                            runOnUiThread(() ->
                                    showCellPhotoAdjustDialog(
                                            target, bitmap,
                                            sh, sz, idx,
                                            dList, uri)
                            );
                        }

                        @Override
                        public void onLoadCleared(
                                @Nullable Drawable p) {
                        }
                    });

            pendingGridCellTarget = null;
            pendingGridCellDataList = null;
            pendingGridCellIdx = -1;
        }


        if (requestCode == REQUEST_GRID_MULTI_PHOTO &&
                resultCode == RESULT_OK &&
                data != null) {

            if (pendingGridMultiContainer == null ||
                    pendingGridMultiDataList == null) {
                Toast.makeText(this, "Grid target મળ્યો નહીં", Toast.LENGTH_SHORT).show();
                return;
            }

            ArrayList<Uri> selectedUris = new ArrayList<>();

            // Multiple images
            if (data.getClipData() != null) {
                android.content.ClipData clipData = data.getClipData();

                for (int i = 0; i < clipData.getItemCount(); i++) {
                    Uri uri = clipData.getItemAt(i).getUri();
                    if (uri != null) {
                        selectedUris.add(uri);

                        try {
                            getContentResolver().takePersistableUriPermission(
                                    uri,
                                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                            );
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

            } else if (data.getData() != null) {
                // Single image selected in multi picker
                Uri uri = data.getData();
                selectedUris.add(uri);

                try {
                    getContentResolver().takePersistableUriPermission(
                            uri,
                            Intent.FLAG_GRANT_READ_URI_PERMISSION
                    );
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (selectedUris.isEmpty()) {
                Toast.makeText(this, "કોઈ photo select નથી", Toast.LENGTH_SHORT).show();
                return;
            }

            GridMeta m = (GridMeta) pendingGridMultiContainer.getTag(R.id.btn_grid_frame);

            if (m == null) {
                Toast.makeText(this, "Grid meta મળ્યો નહીં", Toast.LENGTH_SHORT).show();
                return;
            }

            int start = pendingGridMultiStartIdx;
            int totalCells = pendingGridMultiDataList.size();

            int setCount = 0;

            for (int i = 0; i < selectedUris.size(); i++) {
                int cellIndex = start + i;

                if (cellIndex >= totalCells) {
                    break;
                }

                Uri uri = selectedUris.get(i);

                try {
                    pendingGridMultiDataList
                            .get(cellIndex)
                            .put("photoUri", uri.toString());

                    setCount++;

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            rebuildGridFrame(
                    pendingGridMultiContainer,
                    m.rows,
                    m.cols,
                    m.shape,
                    m.cellSizePx,
                    m.showName,
                    m.showInfo,
                    pendingGridMultiDataList
            );

            exportToJson();

            Toast.makeText(this,
                    "✅ " + setCount + " photos grid માં set થઈ ગયા",
                    Toast.LENGTH_SHORT).show();

            pendingGridMultiContainer = null;
            pendingGridMultiDataList = null;
            pendingGridMultiStartIdx = 0;
        }

        if (requestCode == REQUEST_GRID_LIST_ACTIVITY && data != null) {

            String action = data.getStringExtra(GridListActivity.RESULT_ACTION);

            if ("NO_CHANGE".equals(action) || action == null) {
                currentGridListTarget = null;
                return;
            }

            if ("UPDATE".equals(action) && currentGridListTarget != null) {

                String cellsJson = data.getStringExtra(GridListActivity.RESULT_CELLS_JSON);
                if (cellsJson == null || cellsJson.isEmpty()) {
                    currentGridListTarget = null;
                    return;
                }

                // ✅ Loader start
                showGridLoader("📷 Grid update થઈ રહ્યી છે...");

                final RelativeLayout targetGrid = currentGridListTarget;
                currentGridListTarget = null;

                try {
                    org.json.JSONArray arr = new org.json.JSONArray(cellsJson);
                    final List<JSONObject> updatedCells = new ArrayList<>();
                    for (int i = 0; i < arr.length(); i++) {
                        updatedCells.add(arr.getJSONObject(i));
                    }

                    int newRows = data.getIntExtra(GridListActivity.EXTRA_ROWS, 3);
                    int newCols = data.getIntExtra(GridListActivity.EXTRA_COLS, 4);
                    String newShape = data.getStringExtra(GridListActivity.EXTRA_SHAPE);
                    if (newShape == null) newShape = "ROUNDED";
                    int newCellSize = data.getIntExtra(GridListActivity.EXTRA_CELL_SIZE, 200);
                    boolean showName = data.getBooleanExtra(GridListActivity.EXTRA_SHOW_NAME, true);
                    boolean showInfo = data.getBooleanExtra(GridListActivity.EXTRA_SHOW_INFO, true);

                    // ── Mask cells count
                    int maskCount = 0;
                    for (JSONObject cell : updatedCells) {
                        if (!cell.optString("frameMask", "").isEmpty()
                                || !cell.optString("frameTop", "").isEmpty()) {
                            maskCount++;
                        }
                    }
                    final int totalMaskCells = maskCount;

                    // ── Update loader message
                    if (totalMaskCells > 0 && gridLoaderOverlay != null) {
                        android.widget.LinearLayout box =
                                (android.widget.LinearLayout) gridLoaderOverlay.getChildAt(0);
                        if (box != null && box.getChildCount() >= 2) {
                            android.view.View tv = box.getChildAt(1);
                            if (tv instanceof android.widget.TextView) {
                                ((android.widget.TextView) tv).setText(
                                        "🖼 " + totalMaskCells + " masks load થઈ રહ્યા છે...");
                            }
                        }
                    }

                    rebuildGridFrame(
                            targetGrid,
                            newRows, newCols, newShape,
                            newCellSize, showName, showInfo,
                            updatedCells
                    );

                    // ── Hide loader after Glide async completes
                    // mask cells = 2s (Glide load time), no mask = 0.5s
                    mainLayout.postDelayed(
                            this::hideGridLoader,
                            totalMaskCells > 0 ? 2000 : 500);

                } catch (Exception e) {
                    e.printStackTrace();
                    hideGridLoader();
                    Toast.makeText(this,
                            "Grid update error: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            }
        }


        if (requestCode == REQUEST_GRID_LIST) {

            if (data == null) return;

            String action = data.getStringExtra(
                    com.example.newcardmaker.Activity.GridListActivity.RESULT_ACTION);
            int gridIndex = data.getIntExtra(
                    com.example.newcardmaker.Activity.GridListActivity.RESULT_GRID_INDEX, 0);

            // User e "NO_CHANGE" / Cancel choose karyu → koi change nahi
            if (!"UPDATE".equals(action)) return;

            // ── Result OK + UPDATE ─────────────────────────────────────────
            if (resultCode == RESULT_OK) {

                // 1. Updated cells JSON retrieve karo
                String updatedCellsJson = data.getStringExtra(
                        com.example.newcardmaker.Activity.GridListActivity.RESULT_CELLS_JSON);

                // 2. Grid meta retrieve karo
                int newRows = data.getIntExtra(
                        com.example.newcardmaker.Activity.GridListActivity.EXTRA_ROWS, 3);
                int newCols = data.getIntExtra(
                        com.example.newcardmaker.Activity.GridListActivity.EXTRA_COLS, 4);
                String newShape = data.getStringExtra(
                        com.example.newcardmaker.Activity.GridListActivity.EXTRA_SHAPE);
                int newCellSize = data.getIntExtra(
                        com.example.newcardmaker.Activity.GridListActivity.EXTRA_CELL_SIZE, 200);
                boolean newShowName = data.getBooleanExtra(
                        com.example.newcardmaker.Activity.GridListActivity.EXTRA_SHOW_NAME, true);
                boolean newShowInfo = data.getBooleanExtra(
                        com.example.newcardmaker.Activity.GridListActivity.EXTRA_SHOW_INFO, true);

                if (updatedCellsJson == null || updatedCellsJson.isEmpty()) return;

                // 3. Map ma store karo (next open mate)
                gridCellsMap.put(gridIndex, updatedCellsJson);

                // 4. Bitmap cache clear karo (stale bitmap hatavo)
                android.graphics.Bitmap old = gridBitmapCache.get(gridIndex);
                if (old != null && !old.isRecycled()) old.recycle();
                gridBitmapCache.remove(gridIndex);

                // 5. Grid bitmap re-render karo
                renderGridBitmap(
                        gridIndex, updatedCellsJson,
                        newRows, newCols, newShape,
                        newCellSize, newShowName, newShowInfo);
            }
        }


        if (requestCode == REQUEST_FRAME_PHOTO_ADJUST
                && resultCode == RESULT_OK && data != null
                && currentFrameTargetSticker != null) {
            Uri uri = data.getData();
            if (uri != null) {
                final ImageView target = currentFrameTargetSticker;
                Object mTag = target.getTag(R.id.btn_location);
                Object tTag = target.getTag(R.id.btn_add_sticker);
                Object cTag = target.getTag(R.id.seek_multi_size);
                Glide.with(this).asBitmap().load(uri)
                        .into(new CustomTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(@NonNull Bitmap bmp,
                                                        @Nullable Transition<? super Bitmap> t) {
                                showFramePhotoAdjustDialog(bmp, uri,
                                        mTag != null ? mTag.toString() : pendingMaskUrl,
                                        tTag != null ? tTag.toString() : pendingImageTopUrl,
                                        cTag instanceof Integer ? (int) cTag : pendingFrameOverlayColor,
                                        target);
                            }

                            @Override
                            public void onLoadCleared(@Nullable Drawable p) {
                            }
                        });
            }
        }

        if (requestCode == REQUEST_TEXT_BG_IMAGE
                && resultCode == RESULT_OK && data != null) {

            Uri selectedUri = data.getData();
            if (selectedUri != null && pendingTextBgTarget != null) {
                applyTextShaderImage(selectedUri, pendingTextBgTarget);
            }
            pendingTextBgTarget = null;
        }

    }

    private void applyTextShaderImage(
            Uri imageUri,
            final StrokeTextView targetView) {

        if (imageUri == null || targetView == null) return;

        Glide.with(this)
                .asBitmap()
                .load(imageUri)
                .into(new CustomTarget<Bitmap>() {

                    @Override
                    public void onResourceReady(
                            @NonNull Bitmap bitmap,
                            @Nullable Transition<? super Bitmap> t) {

                        runOnUiThread(() -> {
                            try {
                                int vw = targetView.getWidth();
                                int vh = targetView.getHeight();
                                if (vw <= 0) vw = 500;
                                if (vh <= 0) vh = 200;

                                Bitmap scaled = Bitmap.createScaledBitmap(
                                        bitmap, vw, vh, true);

                                // ✅ BitmapShader
                                android.graphics.BitmapShader shader =
                                        new android.graphics.BitmapShader(
                                                scaled,
                                                android.graphics.Shader.TileMode.CLAMP,
                                                android.graphics.Shader.TileMode.CLAMP);

                                // ✅ StrokeTextView ની method use
                                targetView.setTextShader(shader);

                                // ✅ Gradient clear — conflict avoid
                                targetView.setTextGradient(null);
                                targetView.setTag(R.id.tv_move_speed, null);

                                // ✅ Tag store
                                targetView.setTag(
                                        R.id.btn_sticker_gallery,
                                        "SHADER:" + imageUri.toString());

                                exportToJson();

                                Toast.makeText(MainActivity.this,
                                        "✅ Text image effect apply!",
                                        Toast.LENGTH_SHORT).show();

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        });
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable p) {
                    }
                });
    }


    private void renderGridBitmap(int gridIndex, String cellsJson,
                                  int rows, int cols, String shape,
                                  int cellSizePx, boolean showName, boolean showInfo) {

        // Background thread ma heavy bitmap work karo
        new Thread(() -> {

            try {
                org.json.JSONArray arr = new org.json.JSONArray(cellsJson);

                int totalCells = arr.length();
                if (totalCells == 0) return;

                // Cell size
                int sz = cellSizePx > 0 ? cellSizePx : 200;

                // Gap between cells (dp convert)
                int gap = dpToPx(6);

                // Grid bitmap size calculate karo
                int gridW = cols * sz + (cols - 1) * gap;
                int gridH = rows * sz + (rows - 1) * gap;

                android.graphics.Bitmap gridBmp = android.graphics.Bitmap.createBitmap(
                        gridW, gridH, android.graphics.Bitmap.Config.ARGB_8888);
                android.graphics.Canvas canvas = new android.graphics.Canvas(gridBmp);
                canvas.drawColor(android.graphics.Color.TRANSPARENT);

                android.graphics.Paint paint = new android.graphics.Paint(
                        android.graphics.Paint.ANTI_ALIAS_FLAG
                                | android.graphics.Paint.FILTER_BITMAP_FLAG);

                // Drek cell draw karo
                for (int i = 0; i < totalCells; i++) {
                    org.json.JSONObject cell = arr.getJSONObject(i);

                    int row = i / cols;
                    int col = i % cols;
                    int left = col * (sz + gap);
                    int top = row * (sz + gap);

                    // Cell bitmap get karo (photoBitmap base64 decode)
                    android.graphics.Bitmap cellBmp = decodeCellBitmap(cell, sz, shape);

                    if (cellBmp != null) {
                        canvas.drawBitmap(cellBmp, left, top, paint);

                        // Name label draw karo
                        if (showName) {
                            String name = cell.optString("name", "");
                            if (!name.isEmpty()) {
                                drawCellLabel(canvas, name, left, top + sz, sz,
                                        android.graphics.Color.parseColor("#212121"), 13f);
                            }
                        }

                        // Info label draw karo
                        if (showInfo) {
                            String info = cell.optString("info", "");
                            if (!info.isEmpty()) {
                                int labelY = top + sz + (showName ? dpToPx(18) : 0);
                                drawCellLabel(canvas, info, left, labelY, sz,
                                        infoColor(info), 11f);
                            }
                        }

                        if (!cellBmp.isRecycled()) cellBmp.recycle();
                    } else {
                        // No photo → placeholder draw karo
                        drawPlaceholder(canvas, paint, left, top, sz, shape, col + row);
                    }
                }

                // Bitmap cache ma store karo
                gridBitmapCache.put(gridIndex, gridBmp);

                // UI thread ma grid view update karo
                runOnUiThread(() -> updateGridView(gridIndex, gridBmp));

            } catch (org.json.JSONException e) {
                e.printStackTrace();
                runOnUiThread(() ->
                        android.widget.Toast.makeText(this,
                                "Grid render error: " + e.getMessage(),
                                android.widget.Toast.LENGTH_SHORT).show());
            }

        }).start();
    }


    private void showFramePhotoAdjustDialog(
            final Bitmap originalBitmap,
            final Uri photoUri,
            final String maskUrl,
            final String topUrl,
            final int overlayColor,
            final ImageView targetSticker) {

        // ── Full-screen dialog
        android.app.Dialog dialog = new android.app.Dialog(
                this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);

        RelativeLayout root = new RelativeLayout(this);
        root.setBackgroundColor(Color.parseColor("#DD000000"));
        dialog.setContentView(root);

        // ── Title
        TextView tvTitle = new TextView(this);
        tvTitle.setText("📷  Pinch Zoom + Drag  •  Photo Adjust");
        tvTitle.setTextColor(Color.WHITE);
        tvTitle.setTextSize(13f);
        tvTitle.setGravity(Gravity.CENTER);
        tvTitle.setBackgroundColor(Color.parseColor("#BB1565C0"));
        tvTitle.setPadding(16, 44, 16, 14);
        RelativeLayout.LayoutParams titleLp = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        titleLp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        root.addView(tvTitle, titleLp);

        // ── Preview square (fits screen)
        int screenW = getResources().getDisplayMetrics().widthPixels;
        int screenH = getResources().getDisplayMetrics().heightPixels;
        int previewSize = (int) (Math.min(screenW, screenH) * 0.85f);

        // ── Async bitmap holders
        final Bitmap[] maskHolder = {null};
        final Bitmap[] topHolder = {null};

        // ── Transform matrix
        final android.graphics.Matrix matrix = new android.graphics.Matrix();
        final android.graphics.Matrix savedMatrix = new android.graphics.Matrix();

        // ── Live preview View
        final View previewView = new View(this) {
            @Override
            protected void onDraw(Canvas canvas) {
                super.onDraw(canvas);
                int w = getWidth();
                int h = getHeight();
                if (w <= 0 || h <= 0) return;

                // Dark background
                canvas.drawColor(Color.parseColor("#1A1A1A"));

                Paint paint = new Paint(
                        Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);

                // ── Step 1: Render photo through matrix
                Bitmap photoBmp = Bitmap.createBitmap(
                        w, h, Bitmap.Config.ARGB_8888);
                Canvas photoCanvas = new Canvas(photoBmp);
                photoCanvas.drawBitmap(originalBitmap, matrix, paint);

                // ── Step 2: Apply mask
                Bitmap resultBmp = Bitmap.createBitmap(
                        w, h, Bitmap.Config.ARGB_8888);
                Canvas resultCanvas = new Canvas(resultBmp);

                if (maskHolder[0] != null) {
                    Bitmap scaledMask = Bitmap.createScaledBitmap(
                            maskHolder[0], w, h, true);
                    boolean transparent = hasTransparentPixels(scaledMask);

                    if (transparent) {
                        // Transparent mask: photo shows inside holes
                        resultCanvas.drawBitmap(photoBmp, 0, 0, paint);
                        Paint dstIn = new Paint(Paint.ANTI_ALIAS_FLAG);
                        dstIn.setXfermode(new PorterDuffXfermode(
                                PorterDuff.Mode.DST_IN));
                        resultCanvas.drawBitmap(scaledMask, 0, 0, dstIn);
                        dstIn.setXfermode(null);
                    } else {
                        // Opaque mask: shape stencil
                        resultCanvas.drawBitmap(scaledMask, 0, 0, paint);
                        Paint srcIn = new Paint(Paint.ANTI_ALIAS_FLAG);
                        srcIn.setXfermode(new PorterDuffXfermode(
                                PorterDuff.Mode.SRC_IN));
                        resultCanvas.drawBitmap(photoBmp, 0, 0, srcIn);
                        srcIn.setXfermode(null);
                    }
                    scaledMask.recycle();
                } else {
                    // Mask not loaded yet — show photo directly
                    resultCanvas.drawBitmap(photoBmp, 0, 0, paint);
                }

                canvas.drawBitmap(resultBmp, 0, 0, paint);

                // ── Step 3: Top overlay
                if (topHolder[0] != null) {
                    Bitmap scaledTop = Bitmap.createScaledBitmap(
                            topHolder[0], w, h, true);
                    if (overlayColor != Color.TRANSPARENT && overlayColor != 0) {
                        Bitmap tinted = applyColorTint(scaledTop, overlayColor);
                        canvas.drawBitmap(tinted, 0, 0, paint);
                        tinted.recycle();
                    } else {
                        canvas.drawBitmap(scaledTop, 0, 0, paint);
                    }
                    scaledTop.recycle();
                }

                // Cleanup
                photoBmp.recycle();
                resultBmp.recycle();
            }
        };
        previewView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        RelativeLayout.LayoutParams previewLp =
                new RelativeLayout.LayoutParams(previewSize, previewSize);
        previewLp.addRule(RelativeLayout.CENTER_IN_PARENT);
        root.addView(previewView, previewLp);

        // ── Invisible gesture capture layer (same size, same position)
        final ImageView gestureLayer = new ImageView(this);
        gestureLayer.setImageBitmap(originalBitmap);
        gestureLayer.setScaleType(ImageView.ScaleType.MATRIX);
        gestureLayer.setAlpha(0f);
        root.addView(gestureLayer, previewLp);

        // ── Initial fit: fill the preview square
        gestureLayer.post(() -> {
            float imgW = originalBitmap.getWidth();
            float imgH = originalBitmap.getHeight();
            if (imgW <= 0 || imgH <= 0) return;

            float scale = Math.max(
                    (float) previewSize / imgW,
                    (float) previewSize / imgH);
            float dx = (previewSize - imgW * scale) / 2f;
            float dy = (previewSize - imgH * scale) / 2f;

            matrix.setScale(scale, scale);
            matrix.postTranslate(dx, dy);
            gestureLayer.setImageMatrix(matrix);
            savedMatrix.set(matrix);
            previewView.invalidate();
        });

        // ── Load mask bitmap async
        if (!maskUrl.isEmpty()) {
            Glide.with(this).asBitmap().load(maskUrl)
                    .into(new CustomTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(
                                @NonNull Bitmap bmp,
                                @Nullable Transition<? super Bitmap> t) {
                            maskHolder[0] = bmp;
                            previewView.invalidate();
                        }

                        @Override
                        public void onLoadCleared(
                                @Nullable android.graphics.drawable.Drawable p) {
                        }
                    });
        }

        // ── Load top overlay bitmap async
        if (!topUrl.isEmpty()) {
            Glide.with(this).asBitmap().load(topUrl)
                    .into(new CustomTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(
                                @NonNull Bitmap bmp,
                                @Nullable Transition<? super Bitmap> t) {
                            topHolder[0] = bmp;
                            previewView.invalidate();
                        }

                        @Override
                        public void onLoadCleared(
                                @Nullable android.graphics.drawable.Drawable p) {
                        }
                    });
        }

        // ── Touch gesture state
        final float[] lastTX = {0f};
        final float[] lastTY = {0f};
        final float[] pivotX = {0f};
        final float[] pivotY = {0f};
        final float[] pinchDist = {0f};
        final int[] mode = {0}; // 1=drag 2=pinch

        gestureLayer.setOnTouchListener((v, event) -> {
            switch (event.getActionMasked()) {

                case MotionEvent.ACTION_DOWN:
                    savedMatrix.set(matrix);
                    lastTX[0] = event.getX();
                    lastTY[0] = event.getY();
                    mode[0] = 1;
                    break;

                case MotionEvent.ACTION_POINTER_DOWN:
                    if (event.getPointerCount() >= 2) {
                        pinchDist[0] = fingerSpacing(event);
                        if (pinchDist[0] > 10f) {
                            savedMatrix.set(matrix);
                            fingerMidPoint(pivotX, pivotY, event);
                            mode[0] = 2;
                        }
                    }
                    break;

                case MotionEvent.ACTION_MOVE:
                    if (mode[0] == 1) {
                        // Drag
                        float dx = event.getX() - lastTX[0];
                        float dy = event.getY() - lastTY[0];
                        matrix.set(savedMatrix);
                        matrix.postTranslate(dx, dy);
                        gestureLayer.setImageMatrix(matrix);
                        previewView.invalidate();

                    } else if (mode[0] == 2
                            && event.getPointerCount() >= 2) {
                        // Pinch zoom
                        float newDist = fingerSpacing(event);
                        if (newDist > 10f) {
                            float sf = newDist / pinchDist[0];
                            matrix.set(savedMatrix);
                            matrix.postScale(sf, sf, pivotX[0], pivotY[0]);
                            gestureLayer.setImageMatrix(matrix);
                            previewView.invalidate();
                        }
                    }
                    break;

                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_POINTER_UP:
                    mode[0] = 0;
                    break;
            }
            return true;
        });

        // ── Info label (below preview)
        TextView tvHint = new TextView(this);
        tvHint.setText("✌ Pinch = Zoom   ☝ Drag = Move");
        tvHint.setTextColor(Color.parseColor("#BBBBBB"));
        tvHint.setTextSize(12f);
        tvHint.setGravity(Gravity.CENTER);
        tvHint.setPadding(0, 12, 0, 0);
        RelativeLayout.LayoutParams hintLp =
                new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.MATCH_PARENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT);
        hintLp.addRule(RelativeLayout.BELOW, previewView.getId());
        // note: since we don't set an id on previewView, use ALIGN_PARENT_BOTTOM
        // with bottom margin instead:
        RelativeLayout.LayoutParams hintLp2 =
                new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.MATCH_PARENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT);
        hintLp2.addRule(RelativeLayout.ABOVE, /* btn row id placeholder */ 0);
        hintLp2.bottomMargin = dpToPx(72);
        hintLp2.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        root.addView(tvHint, hintLp2);

        // ── Bottom button row
        LinearLayout btnRow = new LinearLayout(this);
        btnRow.setOrientation(LinearLayout.HORIZONTAL);
        btnRow.setGravity(Gravity.CENTER);
        btnRow.setPadding(16, 16, 16, 40);
        btnRow.setBackgroundColor(Color.parseColor("#BB000000"));

        RelativeLayout.LayoutParams btnRowLp =
                new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.MATCH_PARENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT);
        btnRowLp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        root.addView(btnRow, btnRowLp);

        // Reset
        Button btnReset = makeDlgBtn("↺ Reset", Color.parseColor("#546E7A"));
        btnReset.setOnClickListener(v -> {
            gestureLayer.post(() -> {
                float iW = originalBitmap.getWidth();
                float iH = originalBitmap.getHeight();
                float sc = Math.max(
                        (float) previewSize / iW,
                        (float) previewSize / iH);
                float dx = (previewSize - iW * sc) / 2f;
                float dy = (previewSize - iH * sc) / 2f;
                matrix.setScale(sc, sc);
                matrix.postTranslate(dx, dy);
                gestureLayer.setImageMatrix(matrix);
                savedMatrix.set(matrix);
                previewView.invalidate();
            });
        });
        btnRow.addView(btnReset, makeBtnLp());

        // Cancel
        Button btnCancel = makeDlgBtn("✕ Cancel", Color.parseColor("#C62828"));
        btnCancel.setOnClickListener(v -> dialog.dismiss());
        btnRow.addView(btnCancel, makeBtnLp());

        // Apply
        Button btnApply = makeDlgBtn("✅ Apply", Color.parseColor("#1565C0"));
        btnApply.setOnClickListener(v -> {
            dialog.dismiss();
            // Show progress
            Toast.makeText(this, "Photo apply थाय chhe...", Toast.LENGTH_SHORT).show();
            mergeFramePhotoFromMatrix(
                    originalBitmap, matrix, gestureLayer,
                    maskUrl, topUrl, overlayColor, targetSticker);
        });
        btnRow.addView(btnApply, makeBtnLp());

        dialog.show();
    }

    private void mergeFramePhotoFromMatrix(
            final Bitmap originalBitmap,
            final android.graphics.Matrix matrix,
            final ImageView gestureLayer,
            final String maskUrl,
            final String topUrl,
            final int overlayColor,
            final ImageView targetSticker) {

        // ── Invert matrix to get image-space crop region
        android.graphics.Matrix invertMatrix = new android.graphics.Matrix();
        matrix.invert(invertMatrix);

        int vW = gestureLayer.getWidth();
        int vH = gestureLayer.getHeight();

        float[] corners = {0, 0, vW, 0, vW, vH, 0, vH};
        invertMatrix.mapPoints(corners);

        float minX = Math.min(Math.min(corners[0], corners[2]),
                Math.min(corners[4], corners[6]));
        float minY = Math.min(Math.min(corners[1], corners[3]),
                Math.min(corners[5], corners[7]));
        float maxX = Math.max(Math.max(corners[0], corners[2]),
                Math.max(corners[4], corners[6]));
        float maxY = Math.max(Math.max(corners[1], corners[3]),
                Math.max(corners[5], corners[7]));

        int bW = originalBitmap.getWidth();
        int bH = originalBitmap.getHeight();
        minX = Math.max(0, minX);
        minY = Math.max(0, minY);
        maxX = Math.min(bW, maxX);
        maxY = Math.min(bH, maxY);

        int cropW = (int) (maxX - minX);
        int cropH = (int) (maxY - minY);
        if (cropW <= 0 || cropH <= 0) {
            minX = 0;
            minY = 0;
            cropW = bW;
            cropH = bH;
        }

        final Bitmap cropped = Bitmap.createBitmap(
                originalBitmap, (int) minX, (int) minY, cropW, cropH);

        // ── Load mask, then merge, then update targetSticker
        String loadUrl = !maskUrl.isEmpty() ? maskUrl : topUrl;
        if (loadUrl.isEmpty()) {
            // No frame URLs — just show cropped photo
            targetSticker.setImageBitmap(cropped);
            exportToJson();
            return;
        }

        Glide.with(this).asBitmap().load(loadUrl)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(
                            @NonNull Bitmap maskBitmap,
                            @Nullable Transition<? super Bitmap> t) {

                        int w = maskBitmap.getWidth();
                        int h = maskBitmap.getHeight();

                        // Scale cropped photo to mask size
                        Bitmap userScaled = Bitmap.createScaledBitmap(
                                cropped, w, h, true);

                        // ── Mask apply (DST_IN)
                        Bitmap userMasked = Bitmap.createBitmap(
                                w, h, Bitmap.Config.ARGB_8888);
                        Canvas maskedCanvas = new Canvas(userMasked);
                        Paint paint = new Paint(
                                Paint.ANTI_ALIAS_FLAG
                                        | Paint.FILTER_BITMAP_FLAG);

                        maskedCanvas.drawBitmap(userScaled, 0, 0, paint);

                        Paint dstIn = new Paint(Paint.ANTI_ALIAS_FLAG);
                        dstIn.setXfermode(new PorterDuffXfermode(
                                PorterDuff.Mode.DST_IN));
                        maskedCanvas.drawBitmap(maskBitmap, 0, 0, dstIn);
                        dstIn.setXfermode(null);
                        userScaled.recycle();

                        // ── Keep userMasked copy for color change later
                        Bitmap userMaskedCopy = userMasked.copy(
                                Bitmap.Config.ARGB_8888, false);

                        if (topUrl.isEmpty()) {
                            // No top overlay
                            runOnUiThread(() -> {
                                targetSticker.setImageBitmap(userMasked);
                                targetSticker.setTag(
                                        R.id.tv_size_label, userMaskedCopy);
                                exportToJson();
                                Toast.makeText(MainActivity.this,
                                        "✅ Photo set થઈ ગયો!",
                                        Toast.LENGTH_SHORT).show();
                            });
                            return;
                        }

                        // ── Load top overlay then final merge
                        Glide.with(MainActivity.this).asBitmap().load(topUrl)
                                .into(new CustomTarget<Bitmap>(w, h) {
                                    @Override
                                    public void onResourceReady(
                                            @NonNull Bitmap topBitmap,
                                            @Nullable Transition<
                                                    ? super Bitmap> t2) {

                                        Bitmap result = Bitmap.createBitmap(
                                                w, h, Bitmap.Config.ARGB_8888);
                                        Canvas canvas = new Canvas(result);
                                        Paint p = new Paint(
                                                Paint.ANTI_ALIAS_FLAG
                                                        | Paint.FILTER_BITMAP_FLAG);

                                        // Layer 1: masked user photo
                                        canvas.drawBitmap(userMasked, 0, 0, p);

                                        // Layer 2: top overlay (with optional tint)
                                        Bitmap topScaled =
                                                Bitmap.createScaledBitmap(
                                                        topBitmap, w, h, true);
                                        if (overlayColor != Color.TRANSPARENT
                                                && overlayColor != 0) {
                                            Bitmap tinted = applyColorTint(
                                                    topScaled, overlayColor);
                                            canvas.drawBitmap(tinted, 0, 0, p);
                                            tinted.recycle();
                                        } else {
                                            canvas.drawBitmap(topScaled, 0, 0, p);
                                        }
                                        topScaled.recycle();
                                        userMasked.recycle();

                                        runOnUiThread(() -> {
                                            // ── Update EXISTING sticker ImageView
                                            targetSticker.setImageBitmap(result);
                                            // Store userMasked for future color change
                                            targetSticker.setTag(
                                                    R.id.tv_size_label,
                                                    userMaskedCopy);
                                            // Update overlay color tag
                                            targetSticker.setTag(
                                                    R.id.seek_multi_size,
                                                    overlayColor);
                                            exportToJson();
                                            Toast.makeText(
                                                    MainActivity.this,
                                                    "✅ Photo set થઈ ગયો!",
                                                    Toast.LENGTH_SHORT).show();
                                        });
                                    }

                                    @Override
                                    public void onLoadCleared(
                                            @Nullable android.graphics.drawable.Drawable p) {
                                    }

                                    @Override
                                    public void onLoadFailed(
                                            @Nullable android.graphics.drawable.Drawable e) {
                                        // top fail — show just masked
                                        runOnUiThread(() -> {
                                            targetSticker.setImageBitmap(userMasked);
                                            targetSticker.setTag(
                                                    R.id.tv_size_label, userMaskedCopy);
                                            exportToJson();
                                        });
                                    }
                                });
                    }

                    @Override
                    public void onLoadCleared(
                            @Nullable android.graphics.drawable.Drawable p) {
                    }

                    @Override
                    public void onLoadFailed(
                            @Nullable android.graphics.drawable.Drawable e) {
                        Toast.makeText(MainActivity.this,
                                "Frame mask load failed",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }


    /**
     * Cell JSON ma thi photoBitmap (base64) decode karo
     * + shape clip apply karo
     */
    private android.graphics.Bitmap decodeCellBitmap(
            org.json.JSONObject cell, int sz, String shape) {

        String b64 = cell.optString("photoBitmap", "");

        // ── Base64 decode (mask+photo already merged by GridListActivity) ──
        if (!b64.isEmpty()) {
            try {
                byte[] bytes = android.util.Base64.decode(
                        b64, android.util.Base64.DEFAULT);
                android.graphics.Bitmap bmp = android.graphics.BitmapFactory
                        .decodeByteArray(bytes, 0, bytes.length);
                if (bmp != null) {
                    // Scale to cell size
                    android.graphics.Bitmap scaled =
                            android.graphics.Bitmap.createScaledBitmap(bmp, sz, sz, true);
                    if (scaled != bmp) bmp.recycle();
                    return scaled;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // ── URI fallback (synchronous load — background thread ma che) ──
        String uri = cell.optString("photoUri", "");
        if (!uri.isEmpty()) {
            try {
                android.graphics.Bitmap bmp = com.bumptech.glide.Glide.with(this)
                        .asBitmap()
                        .load(android.net.Uri.parse(uri))
                        .submit(sz, sz)
                        .get();  // blocking — OK in background thread
                if (bmp != null) return bmp;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return null;  // no photo
    }


// ── Step 6: Placeholder Draw ──
// ============================================================================

    private void drawPlaceholder(android.graphics.Canvas canvas,
                                 android.graphics.Paint paint,
                                 int left, int top, int sz,
                                 String shape, int colorSeed) {

        int[] colors = {
                android.graphics.Color.parseColor("#E3F2FD"),
                android.graphics.Color.parseColor("#E8F5E9"),
                android.graphics.Color.parseColor("#FFF3E0"),
                android.graphics.Color.parseColor("#F3E5F5"),
                android.graphics.Color.parseColor("#E0F2F1"),
        };
        paint.setColor(colors[Math.abs(colorSeed) % colors.length]);

        android.graphics.RectF rect = new android.graphics.RectF(
                left, top, left + sz, top + sz);

        if ("CIRCLE".equals(shape)) {
            canvas.drawCircle(left + sz / 2f, top + sz / 2f, sz / 2f, paint);
        } else if ("ROUNDED".equals(shape)) {
            canvas.drawRoundRect(rect, sz * 0.12f, sz * 0.12f, paint);
        } else {
            canvas.drawRect(rect, paint);
        }

        // Camera icon jeva lines draw karo (simple placeholder)
        paint.setColor(android.graphics.Color.parseColor("#BBBBBB"));
        paint.setStyle(android.graphics.Paint.Style.STROKE);
        paint.setStrokeWidth(2f);
        int cx = left + sz / 2;
        int cy = top + sz / 2;
        int r = sz / 6;
        canvas.drawCircle(cx, cy, r, paint);
        paint.setStyle(android.graphics.Paint.Style.FILL);
    }


// ── Step 7: Cell Label Draw ──
// ============================================================================

    private void drawCellLabel(android.graphics.Canvas canvas,
                               String text, int left, int top,
                               int width, int color, float textSizeSp) {

        android.graphics.Paint tp = new android.graphics.Paint(
                android.graphics.Paint.ANTI_ALIAS_FLAG);
        tp.setColor(color);
        tp.setTextSize(spToPx(textSizeSp));
        tp.setTextAlign(android.graphics.Paint.Align.CENTER);
        tp.setTypeface(android.graphics.Typeface.create(
                android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD));

        // Text truncate if too long
        String display = android.text.TextUtils.ellipsize(
                text,
                new android.text.TextPaint(tp),
                width - dpToPx(8),
                android.text.TextUtils.TruncateAt.END).toString();

        canvas.drawText(display, left + width / 2f, top + dpToPx(14), tp);
    }


// ── Step 8: Grid View Update ──
// ============================================================================

    /**
     * Grid bitmap → ImageView / Canvas / View ma set karo
     * IMPORTANT: tara actual grid view reference yahan use karo
     */
    private void updateGridView(int gridIndex, android.graphics.Bitmap gridBmp) {

        android.widget.Toast.makeText(
                this, "✅ Grid " + (gridIndex + 1) + " updated!",
                android.widget.Toast.LENGTH_SHORT).show();


    }


// ── Step 9: Utility Methods ──
// ============================================================================

    private int spToPx(float sp) {
        return (int) (sp * getResources().getDisplayMetrics().scaledDensity + 0.5f);
    }

    private int infoColor(String s) {
        try {
            float p = Float.parseFloat(
                    s.replace("%", "").replace(",", ".").trim());
            if (p >= 75f) return android.graphics.Color.parseColor("#2E7D32");
            if (p >= 50f) return android.graphics.Color.parseColor("#1565C0");
            if (p >= 25f) return android.graphics.Color.parseColor("#FF9800");
            return android.graphics.Color.parseColor("#C62828");
        } catch (Exception e) {
            return android.graphics.Color.parseColor("#FF9800");
        }
    }


    // ── Gallery image → location icon
    private void addCustomImageLocation(String imgPath) {
        ImageView iv = new ImageView(this);
        Glide.with(this).load(new File(imgPath)).into(iv);
        iv.setTag(R.id.btn_set_background, "file://" + imgPath);
        iv.setTag(R.id.btn_location, "");
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(150, 150);
        lp.addRule(RelativeLayout.CENTER_IN_PARENT);
        iv.setLayoutParams(lp);
        applyTouchListenerForSticker(iv);
        mainLayout.addView(iv);
        selectView(iv);
    }

    // ── Preset icon → location
    private void addIconLocation(int resId) {
        ImageView iv = new ImageView(this);
        iv.setImageResource(resId);
        iv.setTag(R.id.btn_set_background, "RES_" + resId);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(120, 120);
        lp.addRule(RelativeLayout.CENTER_IN_PARENT);
        iv.setLayoutParams(lp);
        applyTouchListenerForSticker(iv);
        mainLayout.addView(iv);
        selectView(iv);
    }


    private void loadFramesDirectly(RecyclerView frameRV, ProgressBar progressBar) {

        // ── ✅ Dummy Views — NullPointerException avoid
        LinearLayout dummyLlEmpty = new LinearLayout(this);
        dummyLlEmpty.setVisibility(View.GONE);

        TextView dummyTvEmpty = new TextView(this);

        // State reset
        invite_photo_frame.frame_arrayList = new ArrayList<>();
        invite_photo_frame.frame_arrayListTemp = new ArrayList<>();
        invite_photo_frame.frame_isOver = false;
        invite_photo_frame.frame_isScroll = false;
        invite_photo_frame.frame_page = 1;
        invite_photo_frame.frame_cidddddd = "8"; // ✅ default catId — API જે accept કરે
        invite_photo_frame.frame_recyclerView = frameRV;
        invite_photo_frame.frame_progressBar = progressBar;
        invite_photo_frame.frame_ll_empty = dummyLlEmpty;  // ✅ null નહીં
        invite_photo_frame.frame_tv_empty = dummyTvEmpty;  // ✅ null નહીં
        invite_photo_frame.frame_button_empty = null;
        invite_photo_frame.frame_adapterImageQuotes = null;

        // ── Methods init
        invite_photo_frame.frame_methods = new invite_Methods(this);

        // ── LayoutManager
        StaggeredGridLayoutManager lLayout = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        frameRV.setLayoutManager(lLayout);
        invite_photo_frame.frame_lLayout = lLayout;

        // ── Method
        invite_photo_frame.frame_selectmethod = invite_AppConstants.METHOD_FRAME_ALL;

        // ── ✅ Load
        invite_photo_frame.loadQuotesByCat_1(invite_AppConstants.METHOD_FRAME_ALL, this);

        // ── Infinite scroll
        invite_EndlessRecyclerViewScrollListener1 scrollListener = new invite_EndlessRecyclerViewScrollListener1(lLayout) {
            @Override
            public void onLoadMore(int p, int totalItemsCount, RecyclerView view) {
                if (!invite_photo_frame.frame_isOver && !invite_photo_frame.frame_isScroll) {
                    invite_photo_frame.frame_isScroll = true;
                    new Handler().postDelayed(() -> invite_photo_frame.loadQuotesByCat_1(invite_AppConstants.METHOD_FRAME_ALL, MainActivity.this), 500);
                }
            }
        };
        frameRV.addOnScrollListener(scrollListener);
        invite_photo_frame.frame_scrollListener = scrollListener;
    }


    private void addMergedFrameToLayout(Bitmap mergedBitmap, String frameUrl, Bitmap userMaskedBitmap) { // ✅ extra param
        if (mergedBitmap == null) return;

        final ImageView framedImage = new ImageView(this);
        framedImage.setImageBitmap(mergedBitmap);
        framedImage.setScaleType(ImageView.ScaleType.FIT_CENTER);
        framedImage.setBackgroundColor(Color.TRANSPARENT);

        framedImage.setTag(R.id.btn_set_background, "FRAMED_IMAGE");
        framedImage.setTag(R.id.btn_sticker_gallery, frameUrl);
        framedImage.setTag(R.id.btn_location, pendingMaskUrl);
        framedImage.setTag(R.id.btn_add_sticker, pendingImageTopUrl);
        framedImage.setTag(R.id.seek_multi_size, pendingFrameOverlayColor);

        // ✅ User+Mask bitmap store — color change re-apply માટે
        framedImage.setTag(R.id.tv_size_label, userMaskedBitmap); // Bitmap store

        int size = (int) (Math.min(mainLayout.getWidth(), mainLayout.getHeight()) * 0.8f);
        if (size <= 0) size = 600;

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(size, size);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        framedImage.setLayoutParams(params);

        applyTouchListenerForSticker(framedImage);
        mainLayout.addView(framedImage);
        selectView(framedImage);

        Toast.makeText(this, "✅ Photo Frame add થઈ ગયો!", Toast.LENGTH_SHORT).show();
    }


    // ── Bitmap merge helper

    private void addStickerFromUrl(String imageUrl, String catId) {
        final ImageView sticker = new ImageView(this);

        // ✅ Directly load from URL — no download
        Glide.with(this).load(imageUrl).placeholder(android.R.drawable.ic_menu_gallery).into(sticker);

        // ✅ Server URL directly store
        sticker.setTag(R.id.btn_set_background, imageUrl);
        sticker.setTag(R.id.btn_add_sticker, catId);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(300, 300);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        sticker.setLayoutParams(params);
        applyTouchListenerForSticker(sticker);
        mainLayout.addView(sticker);
        selectView(sticker);
    }


    private void addStickerFromResId(int resId) {
        final ImageView sticker = new ImageView(this);
        sticker.setImageResource(resId);
        sticker.setTag(R.id.btn_set_background, "RES_" + resId);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(300, 300);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        sticker.setLayoutParams(params);

        applyTouchListenerForSticker(sticker);
        mainLayout.addView(sticker);
        selectView(sticker);
    }

    private void stingFrame(Uri sourceUri) {
        File outputDir = new File(getCacheDir(), "ucrop");
        if (!outputDir.exists()) outputDir.mkdirs();

        Uri destinationUri = Uri.fromFile(new File(outputDir, "cropped_" + System.currentTimeMillis() + ".jpg"));

        // ✅ Existing frame ની mask URL
        Object maskTag = currentFrameTargetSticker.getTag(R.id.btn_location);
        String maskUrl = maskTag != null ? maskTag.toString() : "";

        Object topTag = currentFrameTargetSticker.getTag(R.id.btn_add_sticker);
        String topUrl = topTag != null ? topTag.toString() : "";

        String ratioUrl = maskUrl.isEmpty() ? topUrl : maskUrl;

        if (ratioUrl.isEmpty()) {
            // Fallback — 1:1
            UCrop.of(sourceUri, destinationUri).withAspectRatio(1, 1).withMaxResultSize(1000, 1000).withOptions(getUCropOptions()).start(this, REQUEST_UCROP);
            return;
        }

        Glide.with(this).asBitmap().load(ratioUrl).into(new CustomTarget<Bitmap>() {
            @Override
            public void onResourceReady(@NonNull Bitmap bmp, @Nullable com.bumptech.glide.request.transition.Transition<? super Bitmap> transition) {

                UCrop.of(sourceUri, destinationUri).withAspectRatio(bmp.getWidth(), bmp.getHeight()).withMaxResultSize(1000, 1000).withOptions(getUCropOptions()).start(MainActivity.this, REQUEST_UCROP);
            }

            @Override
            public void onLoadCleared(@Nullable Drawable p) {
            }

            @Override
            public void onLoadFailed(@Nullable Drawable e) {
                UCrop.of(sourceUri, destinationUri).withAspectRatio(1, 1).withMaxResultSize(1000, 1000).withOptions(getUCropOptions()).start(MainActivity.this, REQUEST_UCROP);
            }
        });
    }


    private void updateExistingFrameWithPhoto(Uri croppedUri, final ImageView targetSticker) {

        Object maskTag = targetSticker.getTag(R.id.btn_location);
        Object topTag = targetSticker.getTag(R.id.btn_add_sticker);
        Object colorTag = targetSticker.getTag(R.id.seek_multi_size);

        String maskUrl = maskTag != null ? maskTag.toString() : "";
        String topUrl = topTag != null ? topTag.toString() : "";
        int color = colorTag instanceof Integer ? (int) colorTag : Color.TRANSPARENT;

        File croppedFile = new File(croppedUri.getPath());

        Toast.makeText(this, "Photo set થઈ રહ્યો છે...", Toast.LENGTH_SHORT).show();

        // ── Mask load
        Glide.with(this).asBitmap().load(maskUrl.isEmpty() ? topUrl : maskUrl).into(new CustomTarget<Bitmap>() {
            @Override
            public void onResourceReady(@NonNull Bitmap maskBitmap, @Nullable com.bumptech.glide.request.transition.Transition<? super Bitmap> transition) {

                int w = maskBitmap.getWidth();
                int h = maskBitmap.getHeight();

                // ── User photo load
                Glide.with(MainActivity.this).asBitmap().load(croppedFile).into(new CustomTarget<Bitmap>(w, h) {
                    @Override
                    public void onResourceReady(@NonNull Bitmap userBitmap, @Nullable com.bumptech.glide.request.transition.Transition<? super Bitmap> transition) {

                        // ── Top load
                        Glide.with(MainActivity.this).asBitmap().load(topUrl).into(new CustomTarget<Bitmap>(w, h) {
                            @Override
                            public void onResourceReady(@NonNull Bitmap topBitmap, @Nullable com.bumptech.glide.request.transition.Transition<? super Bitmap> transition) {

                                // ✅ pendingFrameOverlayColor = saved color
                                pendingFrameOverlayColor = color;

                                MergeResult result = mergeThreeLayers(userBitmap, maskBitmap, topBitmap, w, h);

                                // ✅ Existing sticker update
                                targetSticker.setImageBitmap(result.finalBitmap);
                                targetSticker.setTag(R.id.tv_size_label, result.userMaskedBitmap);

                                exportToJson();
                                Toast.makeText(MainActivity.this, "✅ Photo set થઈ ગયો!", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onLoadCleared(@Nullable Drawable p) {
                            }

                            @Override
                            public void onLoadFailed(@Nullable Drawable e) {
                                // top વગર
                                pendingFrameOverlayColor = color;
                                MergeResult result = mergeThreeLayers(userBitmap, maskBitmap, null, w, h);
                                targetSticker.setImageBitmap(result.finalBitmap);
                                targetSticker.setTag(R.id.tv_size_label, result.userMaskedBitmap);
                                exportToJson();
                            }
                        });
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable p) {
                    }

                    @Override
                    public void onLoadFailed(@Nullable Drawable e) {
                        try {
                            Bitmap userBmp = BitmapFactory.decodeFile(croppedFile.getAbsolutePath());
                            if (userBmp != null) {
                                pendingFrameOverlayColor = color;
                                MergeResult result = mergeThreeLayers(userBmp, maskBitmap, null, w, h);
                                targetSticker.setImageBitmap(result.finalBitmap);
                                targetSticker.setTag(R.id.tv_size_label, result.userMaskedBitmap);
                                exportToJson();
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                });
            }

            @Override
            public void onLoadCleared(@Nullable Drawable p) {
            }
        });
    }


    private void launchUCropWithFrame(Uri sourceUri) {
        File outputDir = new File(getCacheDir(), "ucrop");
        if (!outputDir.exists()) outputDir.mkdirs();

        Uri destinationUri = Uri.fromFile(new File(outputDir, "cropped_" + System.currentTimeMillis() + ".jpg"));

        // ✅ mask_image ના ratio મુજબ crop
        String ratioUrl = pendingMaskUrl.isEmpty() ? pendingImageTopUrl : pendingMaskUrl;

        Glide.with(this).asBitmap().load(ratioUrl).into(new CustomTarget<Bitmap>() {
            @Override
            public void onResourceReady(@NonNull Bitmap bmp, @Nullable com.bumptech.glide.request.transition.Transition<? super Bitmap> transition) {

                int w = bmp.getWidth();
                int h = bmp.getHeight();

                if (w <= 0 || h <= 0) {
                    Toast.makeText(MainActivity.this, "Frame size invalid", Toast.LENGTH_SHORT).show();
                    return;
                }

                UCrop.of(sourceUri, destinationUri).withAspectRatio(w, h).withMaxResultSize(1000, 1000).withOptions(getUCropOptions()).start(MainActivity.this, REQUEST_UCROP);
            }

            @Override
            public void onLoadCleared(@Nullable Drawable placeholder) {
            }

            @Override
            public void onLoadFailed(@Nullable Drawable errorDrawable) {
                // Fallback — 1:1 ratio
                UCrop.of(sourceUri, destinationUri).withAspectRatio(1, 1).withMaxResultSize(1000, 1000).withOptions(getUCropOptions()).start(MainActivity.this, REQUEST_UCROP);
            }
        });
    }


    // UCrop Options
    private UCrop.Options getUCropOptions() {
        UCrop.Options options = new UCrop.Options();
        options.setCompressionQuality(100);
        options.setHideBottomControls(false);
        options.setFreeStyleCropEnabled(false); // ✅ Fixed ratio
        options.setToolbarTitle("Image Crop કરો");
        options.setToolbarColor(Color.parseColor("#1565C0"));
        // options.setStatusBarColor(Color.parseColor("#0D47A1"));
        options.setActiveControlsWidgetColor(Color.parseColor("#1565C0"));
        return options;
    }


    private Bitmap mergeBitmaps(Bitmap userImage, Bitmap frameBitmap, int width, int height) {

        // ✅ Transparent background
        Bitmap result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

        // ── User image scale
        Bitmap userScaled = Bitmap.createScaledBitmap(userImage, width, height, true);

        // ── Frame scale
        Bitmap frameScaled = Bitmap.createScaledBitmap(frameBitmap, width, height, true);

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);

        // ── Step 1: Frame ના DST_OUT વाળो inverse mask બનાવો
        // Frame transparent pixels → user image show કરવા
        // Frame opaque pixels → user image hide

        // Intermediate bitmap for masking
        Bitmap userMasked = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas maskCanvas = new Canvas(userMasked);

        // A. User image draw
        maskCanvas.drawBitmap(userScaled, 0, 0, paint);

        // B. Frame ના opaque area ને DST_OUT (erase) — frame border area remove
        Paint eraser = new Paint(Paint.ANTI_ALIAS_FLAG);
        eraser.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
        maskCanvas.drawBitmap(frameScaled, 0, 0, eraser);
        eraser.setXfermode(null);

        // ── Step 2: Final canvas
        // A. Masked user image (hole area only)
        canvas.drawBitmap(userMasked, 0, 0, paint);

        // B. Frame overlay (ઉપર)
        canvas.drawBitmap(frameScaled, 0, 0, paint);

        // Cleanup
        userScaled.recycle();
        userMasked.recycle();
        frameScaled.recycle();

        return result;
    }

    // ── Frame ની transparent area = hole mask
    private Bitmap extractHoleMask(Bitmap frame, int width, int height) {
        Bitmap mask = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        Bitmap frameScaled = Bitmap.createScaledBitmap(frame, width, height, true);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int pixel = frameScaled.getPixel(x, y);
                int alpha = Color.alpha(pixel);

                // Transparent pixel = hole (user image area)
                if (alpha < 128) {
                    mask.setPixel(x, y, Color.WHITE); // hole
                } else {
                    mask.setPixel(x, y, Color.BLACK); // frame border
                }
            }
        }

        frameScaled.recycle();
        return mask;
    }

    // invite_sticker_main_category.java માં ઉમેરો
    public void onImageSelected(String imageUrl) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("SELECTED_IMAGE_URL", imageUrl);
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    public void onImageSelectedResId(int resId) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("SELECTED_IMAGE_RES_ID", resId);
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    public void onImageSelectedPath(String filePath) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("SELECTED_IMAGE_PATH", filePath);
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    private void addNewSticker(android.net.Uri uri) {
        final ImageView sticker = new ImageView(this);
        sticker.setImageURI(uri);

        sticker.setTag(R.id.btn_set_background, uri.toString());

        // ડિફોલ્ટ સાઈઝ
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(300, 300);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        sticker.setLayoutParams(params);

        // ટચ લિસ્ટનર (Move અને Select માટે)
        applyTouchListenerForSticker(sticker);

        mainLayout.addView(sticker);
        selectView(sticker); // એડ થતા જ સિલેક્ટ કરો
    }


    @SuppressLint("ClickableViewAccessibility")
    private void applyTouchListenerForSticker(final ImageView imageView) {
        imageView.setOnTouchListener(new View.OnTouchListener() {

            private long msDownTime = 0;
            private boolean msLongPress = false;
            private float msDownX = 0, msDownY = 0;

            // ✅ Tap detect
            private float downRawX = 0, downRawY = 0;

            @Override
            public boolean onTouch(View view, MotionEvent event) {

                if (lockedViews.contains(view)) {
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        showLockPopup(view);
                    }
                    return true;
                }

                if (isMultiSelectMode) {
                    switch (event.getActionMasked()) {
                        case MotionEvent.ACTION_DOWN:
                            msDownTime = System.currentTimeMillis();
                            msLongPress = false;
                            msDownX = event.getRawX();
                            msDownY = event.getRawY();
                            groupMoveStartX = event.getRawX();
                            groupMoveStartY = event.getRawY();
                            groupStartPositions.clear();
                            for (View sel : selectedViews) {
                                groupStartPositions.add(new float[]{sel.getX(), sel.getY()});
                            }
                            isGroupMoving = true;
                            break;

                        case MotionEvent.ACTION_MOVE:
                            long held = System.currentTimeMillis() - msDownTime;
                            float moved = (float) Math.sqrt(Math.pow(event.getRawX() - msDownX, 2) + Math.pow(event.getRawY() - msDownY, 2));
                            if (held > 300 || moved > 10) {
                                msLongPress = true;
                            }
                            if (isGroupMoving && msLongPress) {
                                handleGroupMove(event);
                            }
                            break;

                        case MotionEvent.ACTION_UP:
                            isGroupMoving = false;
                            float dist = (float) Math.sqrt(Math.pow(event.getRawX() - msDownX, 2) + Math.pow(event.getRawY() - msDownY, 2));
                            if (!msLongPress && dist < 10) {
                                if (selectedViews.contains(view)) {
                                    selectedViews.remove(view);
                                    restoreViewBorder(view);
                                    selectedOriginalSizes.clear();
                                    for (View s : selectedViews)
                                        saveOriginalSize(s);
                                } else {
                                    selectedViews.add(view);
                                    applySelectionBorder(view);
                                    saveOriginalSize(view);
                                }
                                showStickerToolbar(imageView);
                                seekMultiSize.setProgress(50);
                                tvSizeLabel.setText("+0.0sp");
                                updateMultiSelectBtnLabel();
                            }
                            break;
                    }
                    return true;
                }

                // ── Normal touch
                rotationGestureDetector.onTouchEvent(event);
                scaleGestureDetector.onTouchEvent(event);

                if (event.getPointerCount() > 1) {
                    selectView(view);
                    return true;
                }

                switch (event.getActionMasked()) {
                    case MotionEvent.ACTION_DOWN:
                        dX = view.getX() - event.getRawX();
                        dY = view.getY() - event.getRawY();
                        // ✅ DOWN position save
                        downRawX = event.getRawX();
                        downRawY = event.getRawY();
                        try {
                            if (frameImageControlsPopup != null
                                    && frameImageControlsPopup.isShowing()
                                    && view.getTag(R.id.btn_set_background) != null
                                    && !"FRAMED_IMAGE".equals(view.getTag(R.id.btn_set_background).toString())) {
                                frameImageControlsPopup.dismiss();
                            }
                        } catch (Exception ignored) {
                        }
                        selectView(view);
                        return true;

                    case MotionEvent.ACTION_MOVE:
                        float newSX = event.getRawX() + dX;
                        float newSY = event.getRawY() + dY;

                        float sMinX = main_image_view.getX();
                        float sMaxX = main_image_view.getX() + main_image_view.getWidth() - view.getWidth();
                        float sMinY = main_image_view.getY();
                        float sMaxY = main_image_view.getY() + main_image_view.getHeight() - view.getHeight();

                        view.setX(Math.max(sMinX, Math.min(newSX, sMaxX)));
                        view.setY(Math.max(sMinY, Math.min(newSY, sMaxY)));
                        break;

                    case MotionEvent.ACTION_UP:
                        // ✅ Tap check — 10px threshold
                        float tapDist = (float) Math.sqrt(Math.pow(event.getRawX() - downRawX, 2) + Math.pow(event.getRawY() - downRawY, 2));

                        if (tapDist < 10) {
                            Object groupTag = view.getTag(R.id.seek_move_speed);
                            if (groupTag != null && groupTag.toString().startsWith("GROUP_")) {
                                // ── Group select
                                selectGroupByView(view);
                                if (!isMultiSelectMode) {
                                    isMultiSelectMode = true;
                                    showMultiSelectPopup();
                                }
                            } else {
                                showStickerToolbar(imageView);
//                                showSelectionControlsForImage(imageView);
                            }
                        }
                        // ✅ Drag — toolbar show નહીં
                        break;
                }
                return true;
            }
        });
    }

    private void showFontPickerDialog(final StrokeTextView targetText) {

        String[][] fonts = {
                {"Default", "DEFAULT"},
                {"Serif", "serif"},
                {"Mono", "monospace"},
                {"Cursive", "cursive"},
                {"Sans-Serif", "sans-serif"},
        };

        // Custom Typeface fonts — add your asset fonts here if needed
        // e.g., fonts loaded from assets/fonts/

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Font Family");

        // Build scrollable list with live preview per font
        ScrollView sv = new ScrollView(this);
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(16, 8, 16, 8);
        sv.addView(root);

        // Current selection tracking
        final String[] currentKey = {
                // Try to detect current
                targetText.getTypeface() != null &&
                        targetText.getTypeface().equals(Typeface.MONOSPACE)
                        ? "monospace"
                        : "DEFAULT"
        };

        for (String[] fontEntry : fonts) {
            final String label = fontEntry[0];
            final String fontKey = fontEntry[1];

            // Row
            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setPadding(12, 10, 12, 10);
            row.setGravity(Gravity.CENTER_VERTICAL);

            LinearLayout.LayoutParams rowLp =
                    new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT);
            rowLp.setMargins(0, 0, 0, 4);
            row.setLayoutParams(rowLp);

            // Highlight current
            boolean isCurrent = fontKey.equals(currentKey[0]);
            row.setBackgroundColor(isCurrent
                    ? Color.parseColor("#E3F2FD")
                    : Color.WHITE);

            // Preview TextView
            TextView preview = new TextView(this);
            preview.setText("Aa Bb 123");
            preview.setTextSize(15);
            preview.setTextColor(Color.parseColor("#212121"));

            // Apply the font for preview
            Typeface tf = getTypefaceForKey(fontKey);
            preview.setTypeface(tf);

            LinearLayout.LayoutParams prevLp =
                    new LinearLayout.LayoutParams(0,
                            LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
            preview.setLayoutParams(prevLp);
            row.addView(preview);

            // Font name label
            TextView name = new TextView(this);
            name.setText(label);
            name.setTextSize(13);
            name.setTextColor(Color.parseColor("#757575"));
            name.setPadding(12, 0, 0, 0);
            row.addView(name);

            // Check mark if selected
            if (isCurrent) {
                TextView check = new TextView(this);
                check.setText("✓");
                check.setTextSize(16);
                check.setTextColor(Color.parseColor("#1565C0"));
                check.setPadding(12, 0, 0, 0);
                row.addView(check);
            }

            root.addView(row);

            // Click — apply font immediately + dismiss
            row.setOnClickListener(v -> {
                Typeface selected = getTypefaceForKey(fontKey);

                // Preserve bold/italic state
                boolean bold = targetText.getTypeface() != null
                        && targetText.getTypeface().isBold();
                boolean italic = targetText.getTypeface() != null
                        && targetText.getTypeface().isItalic();

                int style = Typeface.NORMAL;
                if (bold && italic) style = Typeface.BOLD_ITALIC;
                else if (bold) style = Typeface.BOLD;
                else if (italic) style = Typeface.ITALIC;

                targetText.setTypeface(
                        Typeface.create(selected, style));
                exportToJson();

                Toast.makeText(this,
                        label + " font apply!", Toast.LENGTH_SHORT).show();
            });
        }

        builder.setView(sv);
        builder.setNegativeButton("Close", null);
        builder.show();
    }


    private Typeface getTypefaceForKey(String key) {
        switch (key) {
            case "serif":
                return Typeface.SERIF;
            case "monospace":
                return Typeface.MONOSPACE;
            case "cursive":
                return Typeface.create("cursive", Typeface.NORMAL);
            case "sans-serif":
                return Typeface.create("sans-serif", Typeface.NORMAL);
            default:
                return Typeface.DEFAULT;
        }
    }

    /**
     * Dismiss helper
     */
    private void dismissSelectionControls() {
        try {
            if (selectionControlsPopup != null && selectionControlsPopup.isShowing()) {
                selectionControlsPopup.dismiss();
            }
        } catch (Exception e) {
            selectionControlsPopup = null;
        }
    }


    private void showSelectionControlsForText(StrokeTextView targetView) {

        try {
            if (selectionControlsPopup != null && selectionControlsPopup.isShowing()) {
                selectionControlsPopup.dismiss();
            }
        } catch (Exception e) {
            selectionControlsPopup = null;
        }

        boolean isLocked = lockedViews.contains(targetView);

        LayoutInflater inflater = LayoutInflater.from(this);
        View cv = inflater.inflate(R.layout.layout_selection_controls_text, null);

        selectionControlsPopup = new PopupWindow(cv,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, false);
        selectionControlsPopup.setOutsideTouchable(false);
        selectionControlsPopup.setTouchable(true);
        selectionControlsPopup.setBackgroundDrawable(
                new android.graphics.drawable.ColorDrawable(Color.TRANSPARENT));

        setupDragHandle(cv);

        selOriginalSize = targetView.getTextSize();

        // ════════════════════════════
// HIDE/SHOW TOGGLE
// ════════════════════════════
        TextView btnPanelToggle = cv.findViewById(R.id.btn_panel_toggle);
        LinearLayout panelMainContent = cv.findViewById(R.id.panel_main_content);
        final boolean[] isPanelVisible = {true};

        if (btnPanelToggle != null && panelMainContent != null) {
            btnPanelToggle.setOnClickListener(v -> {
                isPanelVisible[0] = !isPanelVisible[0];
                if (isPanelVisible[0]) {
                    // ✅ Show with animation
                    panelMainContent.setVisibility(View.VISIBLE);
                    panelMainContent.animate()
                            .alpha(1f)
                            .scaleY(1f)
                            .setDuration(200)
                            .start();
                    btnPanelToggle.setText("▼");
                } else {
                    // ✅ Hide with animation
                    panelMainContent.animate()
                            .alpha(0f)
                            .scaleY(0f)
                            .setDuration(200)
                            .withEndAction(() ->
                                    panelMainContent.setVisibility(View.GONE))
                            .start();
                    btnPanelToggle.setText("▲");
                }
            });
        }

// ════════════════════════════
// TAB SWITCHING LOGIC
// ════════════════════════════
        TextView tabAction = cv.findViewById(R.id.tab_action);
        TextView tabSpacing = cv.findViewById(R.id.tab_spacing);
        TextView tabTransformTab = cv.findViewById(R.id.tab_transform);
        TextView tabEffects = cv.findViewById(R.id.tab_effects);
        TextView tabLayout = cv.findViewById(R.id.tab_layout);

        LinearLayout panelAction = cv.findViewById(R.id.panel_tab_action);
        LinearLayout panelSpacing = cv.findViewById(R.id.panel_tab_spacing);
        LinearLayout panelTransformP = cv.findViewById(R.id.panel_tab_transform);
        LinearLayout panelEffects = cv.findViewById(R.id.panel_tab_effects);
        LinearLayout panelLayout = cv.findViewById(R.id.panel_tab_layout);

        TextView[] allTabs = {tabAction, tabSpacing,
                tabTransformTab, tabEffects, tabLayout};
        LinearLayout[] allPanels = {panelAction, panelSpacing,
                panelTransformP, panelEffects, panelLayout};

        // Padding X
        android.widget.SeekBar seekPadX = cv.findViewById(R.id.seek_padding_x);
        android.widget.TextView tvPadXLbl = cv.findViewById(R.id.tv_padding_x_label);
        android.widget.TextView btnPadXM = cv.findViewById(R.id.btn_pad_x_minus);
        android.widget.TextView btnPadXP = cv.findViewById(R.id.btn_pad_x_plus);

// Padding Y
        android.widget.SeekBar seekPadY = cv.findViewById(R.id.seek_padding_y);
        android.widget.TextView tvPadYLbl = cv.findViewById(R.id.tv_padding_y_label);
        android.widget.TextView btnPadYM = cv.findViewById(R.id.btn_pad_y_minus);
        android.widget.TextView btnPadYP = cv.findViewById(R.id.btn_pad_y_plus);

// ── Current padding set
        int strokeE = (int) Math.ceil(targetView.getStrokeWidth()) + 4;
        int curPadX = Math.max(0, targetView.getPaddingLeft() - strokeE);
        int curPadY = Math.max(0, targetView.getPaddingTop() - strokeE);

        if (seekPadX != null) seekPadX.setProgress(Math.min(80, curPadX));
        if (seekPadY != null) seekPadY.setProgress(Math.min(80, curPadY));

        if (tvPadXLbl != null) tvPadXLbl.setText(curPadX + "px");
        if (tvPadYLbl != null) tvPadYLbl.setText(curPadY + "px");

// ── Apply helper
        Runnable applyPadding = () -> {
            int px = seekPadX != null ? seekPadX.getProgress() : curPadX;
            int py = seekPadY != null ? seekPadY.getProgress() : curPadY;
            int se = (int) Math.ceil(targetView.getStrokeWidth()) + 4;

            targetView.setPadding(se + px, se + py, se + px, se + py);

            // ✅ User value tag માં store કરો
            targetView.setTag(R.id.btn_location, new int[]{px, py});

            targetView.requestLayout();
            if (tvPadXLbl != null) tvPadXLbl.setText(px + "px");
            if (tvPadYLbl != null) tvPadYLbl.setText(py + "px");

            exportToJson(); // ✅ દરેક change save
        };

// ── Seekbar listeners
        if (seekPadX != null) {
            seekPadX.setOnSeekBarChangeListener(
                    new android.widget.SeekBar.OnSeekBarChangeListener() {
                        @Override
                        public void onProgressChanged(
                                android.widget.SeekBar s, int p, boolean f) {
                            if (!f) return;
                            applyPadding.run();
                        }

                        @Override
                        public void onStartTrackingTouch(android.widget.SeekBar s) {
                        }

                        @Override
                        public void onStopTrackingTouch(android.widget.SeekBar s) {
                            exportToJson();
                        }
                    });
        }

        if (seekPadY != null) {
            seekPadY.setOnSeekBarChangeListener(
                    new android.widget.SeekBar.OnSeekBarChangeListener() {
                        @Override
                        public void onProgressChanged(
                                android.widget.SeekBar s, int p, boolean f) {
                            if (!f) return;
                            applyPadding.run();
                        }

                        @Override
                        public void onStartTrackingTouch(android.widget.SeekBar s) {
                        }

                        @Override
                        public void onStopTrackingTouch(android.widget.SeekBar s) {
                            exportToJson();
                        }
                    });
        }

// ── +/- buttons
        if (btnPadXM != null) btnPadXM.setOnClickListener(v -> {
            if (seekPadX == null) return;
            seekPadX.setProgress(Math.max(0, seekPadX.getProgress() - 2));
            applyPadding.run();
            exportToJson();
        });
        if (btnPadXP != null) btnPadXP.setOnClickListener(v -> {
            if (seekPadX == null) return;
            seekPadX.setProgress(Math.min(80, seekPadX.getProgress() + 2));
            applyPadding.run();
            exportToJson();
        });
        if (btnPadYM != null) btnPadYM.setOnClickListener(v -> {
            if (seekPadY == null) return;
            seekPadY.setProgress(Math.max(0, seekPadY.getProgress() - 2));
            applyPadding.run();
            exportToJson();
        });
        if (btnPadYP != null) btnPadYP.setOnClickListener(v -> {
            if (seekPadY == null) return;
            seekPadY.setProgress(Math.min(80, seekPadY.getProgress() + 2));
            applyPadding.run();
            exportToJson();
        });

// ── Tab switch helper
        android.view.View.OnClickListener tabClickListener = v -> {
            for (int t = 0; t < allTabs.length; t++) {
                boolean isActive = allTabs[t] == v;

                if (allTabs[t] != null) {
                    allTabs[t].setBackgroundColor(isActive
                            ? Color.parseColor("#1565C0")
                            : Color.parseColor("#BBDEFB"));
                    allTabs[t].setTextColor(isActive
                            ? Color.WHITE
                            : Color.parseColor("#1565C0"));
                }

                if (allPanels[t] != null) {
                    if (isActive) {
                        allPanels[t].setVisibility(View.VISIBLE);
                        allPanels[t].setAlpha(0f);
                        allPanels[t].animate().alpha(1f)
                                .setDuration(150).start();
                    } else {
                        allPanels[t].setVisibility(View.GONE);
                    }
                }
            }
        };

        if (tabAction != null) tabAction.setOnClickListener(tabClickListener);
        if (tabSpacing != null) tabSpacing.setOnClickListener(tabClickListener);
        if (tabTransformTab != null) tabTransformTab.setOnClickListener(tabClickListener);
        if (tabEffects != null) tabEffects.setOnClickListener(tabClickListener);
        if (tabLayout != null) tabLayout.setOnClickListener(tabClickListener);

        // ── Undo/Redo history
        final java.util.ArrayDeque<String> undoStack = new java.util.ArrayDeque<>();
        final java.util.ArrayDeque<String> redoStack = new java.util.ArrayDeque<>();
        undoStack.push(targetView.getText().toString());

        // ════════════════════════════
        // QUICK STYLE TOGGLE BUTTONS
        // ════════════════════════════
        TextView btn_curve = cv.findViewById(R.id.btn_curve);
        TextView btnBold = cv.findViewById(R.id.btn_quick_bold);
        TextView btnItalic = cv.findViewById(R.id.btn_quick_italic);
        TextView btnUnderline = cv.findViewById(R.id.btn_quick_underline);
        TextView btnStrike = cv.findViewById(R.id.btn_quick_strike);
        TextView btnFlipH = cv.findViewById(R.id.btn_quick_flip_h);
        TextView btnFlipV = cv.findViewById(R.id.btn_quick_flip_v);

        // ── Helper: update toggle button highlight
        Runnable updateStyleBtns = () -> {
            boolean isBold = targetView.getTypeface() != null
                    && targetView.getTypeface().isBold();
            boolean isItalic2 = targetView.getTypeface() != null
                    && targetView.getTypeface().isItalic();
            boolean isUnder = (targetView.getPaintFlags()
                    & android.graphics.Paint.UNDERLINE_TEXT_FLAG) != 0;
            boolean isStrike2 = (targetView.getPaintFlags()
                    & android.graphics.Paint.STRIKE_THRU_TEXT_FLAG) != 0;

            if (btnBold != null)
                btnBold.setBackgroundColor(isBold
                        ? Color.parseColor("#1565C0") : Color.parseColor("#E3F2FD"));
            if (btnBold != null)
                btnBold.setTextColor(isBold ? Color.WHITE
                        : Color.parseColor("#1565C0"));

            if (btnItalic != null)
                btnItalic.setBackgroundColor(isItalic2
                        ? Color.parseColor("#1565C0") : Color.parseColor("#E3F2FD"));
            if (btnItalic != null)
                btnItalic.setTextColor(isItalic2 ? Color.WHITE
                        : Color.parseColor("#1565C0"));

            if (btnUnderline != null)
                btnUnderline.setBackgroundColor(isUnder
                        ? Color.parseColor("#1565C0") : Color.parseColor("#E3F2FD"));
            if (btnUnderline != null)
                btnUnderline.setTextColor(isUnder ? Color.WHITE
                        : Color.parseColor("#1565C0"));

            if (btnStrike != null)
                btnStrike.setBackgroundColor(isStrike2
                        ? Color.parseColor("#1565C0") : Color.parseColor("#E3F2FD"));
            if (btnStrike != null)
                btnStrike.setTextColor(isStrike2 ? Color.WHITE
                        : Color.parseColor("#1565C0"));
        };
        updateStyleBtns.run();
        if (btn_curve != null) {
            btn_curve.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    // ════════════════════════════
// ARC CONTROLS
// ════════════════════════════
                    TextView btn_curve = cv.findViewById(R.id.btn_curve);
                    LinearLayout panelArc = cv.findViewById(R.id.panel_arc_controls);
                    SeekBar sbArcAngle = cv.findViewById(R.id.sb_arc_angle);
                    SeekBar sbArcRadius = cv.findViewById(R.id.sb_arc_radius);
                    TextView tvArcAngleVal = cv.findViewById(R.id.tv_arc_angle_val);
                    TextView tvArcRadiusVal = cv.findViewById(R.id.tv_arc_radius_val);
                    TextView btnArcBack = cv.findViewById(R.id.btn_arc_back);
                    TextView btnArcToggleOn = cv.findViewById(R.id.btn_arc_toggle_on);
                    TextView btnArcToggleOff = cv.findViewById(R.id.btn_arc_toggle_off);
                    TextView btnArcDirUp = cv.findViewById(R.id.btn_arc_dir_up);
                    TextView btnArcDirDown = cv.findViewById(R.id.btn_arc_dir_down);
                    TextView btnArcAngleM = cv.findViewById(R.id.btn_arc_angle_minus);
                    TextView btnArcAngleP = cv.findViewById(R.id.btn_arc_angle_plus);
                    TextView btnArcRadiusM = cv.findViewById(R.id.btn_arc_radius_minus);
                    TextView btnArcRadiusP = cv.findViewById(R.id.btn_arc_radius_plus);

// ── Tab row reference
                    LinearLayout tabRow = cv.findViewById(R.id.hsv_tab_row); // ✅ tab row hide karva

// ── Arc toggle UI
                    Runnable updateArcToggleUI = () -> {
                        boolean isArc = targetView.isArcMode();
                        if (btnArcToggleOn != null)
                            btnArcToggleOn.setBackgroundColor(
                                    isArc ? Color.parseColor("#E65100") : Color.parseColor("#BBBBBB"));
                        if (btnArcToggleOff != null)
                            btnArcToggleOff.setBackgroundColor(
                                    isArc ? Color.parseColor("#BBBBBB") : Color.parseColor("#E65100"));
                        if (btnArcDirUp != null)
                            btnArcDirUp.setBackgroundColor(
                                    targetView.isArcUp() ? Color.parseColor("#E65100") : Color.parseColor("#BBBBBB"));
                        if (btnArcDirDown != null)
                            btnArcDirDown.setBackgroundColor(
                                    targetView.isArcUp() ? Color.parseColor("#BBBBBB") : Color.parseColor("#E65100"));
                    };

// ── Show arc panel — badha tabs hide, arc show
                    Runnable showArcPanel = () -> {
                        // ✅ Tab row hide
                        if (tabRow != null) tabRow.setVisibility(View.GONE);

                        // ✅ Badha tab panels hide
                        if (panelAction != null) panelAction.setVisibility(View.GONE);
                        if (panelSpacing != null) panelSpacing.setVisibility(View.GONE);
                        if (panelTransformP != null) panelTransformP.setVisibility(View.GONE);
                        if (panelEffects != null) panelEffects.setVisibility(View.GONE);
                        if (panelLayout != null) panelLayout.setVisibility(View.GONE);

                        // ✅ Arc panel show
                        if (panelArc != null) {
                            panelArc.setVisibility(View.VISIBLE);
                            panelArc.setAlpha(0f);
                            panelArc.animate().alpha(1f).setDuration(150).start();
                        }
                    };

// ── Hide arc panel — tab row + action tab restore
                    Runnable hideArcPanel = () -> {
                        if (panelArc != null) panelArc.setVisibility(View.GONE);

                        // ✅ Tab row restore
                        if (tabRow != null) tabRow.setVisibility(View.VISIBLE);

                        // ✅ Action tab default show
                        if (panelAction != null) {
                            panelAction.setVisibility(View.VISIBLE);
                            panelAction.setAlpha(0f);
                            panelAction.animate().alpha(1f).setDuration(150).start();
                        }

                        // ✅ Tab highlight — action active karo
                        if (tabAction != null) {
                            tabAction.setBackgroundColor(Color.parseColor("#1565C0"));
                            tabAction.setTextColor(Color.WHITE);
                        }
                        if (tabSpacing != null) {
                            tabSpacing.setBackgroundColor(Color.parseColor("#BBDEFB"));
                            tabSpacing.setTextColor(Color.parseColor("#1565C0"));
                        }
                        if (tabTransformTab != null) {
                            tabTransformTab.setBackgroundColor(Color.parseColor("#BBDEFB"));
                            tabTransformTab.setTextColor(Color.parseColor("#1565C0"));
                        }
                        if (tabEffects != null) {
                            tabEffects.setBackgroundColor(Color.parseColor("#BBDEFB"));
                            tabEffects.setTextColor(Color.parseColor("#1565C0"));
                        }
                        if (tabLayout != null) {
                            tabLayout.setBackgroundColor(Color.parseColor("#BBDEFB"));
                            tabLayout.setTextColor(Color.parseColor("#1565C0"));
                        }
                    };

// ── btn_curve click
                    if (btn_curve != null) {
                        btn_curve.setOnClickListener(v1 -> {

                            boolean wasArcMode = targetView.isArcMode();
                            if (!wasArcMode) {
                                targetView.setArcMode(true);
                                targetView.setArcAngle(180f);
                                targetView.setRadius(150f);
                                targetView.setArcUp(true);
                            }

                            if (sbArcAngle != null) {
                                sbArcAngle.setProgress((int) targetView.getArcAngle());
                                if (tvArcAngleVal != null)
                                    tvArcAngleVal.setText((int) targetView.getArcAngle() + "°");
                            }
                            if (sbArcRadius != null) {
                                sbArcRadius.setProgress((int) targetView.getRadius());
                                if (tvArcRadiusVal != null)
                                    tvArcRadiusVal.setText((int) targetView.getRadius() + "");
                            }

                            updateArcToggleUI.run();
                            showArcPanel.run();
                        });
                    }

// ── Back
                    if (btnArcBack != null) {
                        btnArcBack.setOnClickListener(v1 -> {
                            exportToJson();
                            hideArcPanel.run();
                        });
                    }

// ── Arc ON
                    if (btnArcToggleOn != null) {
                        btnArcToggleOn.setOnClickListener(v1 -> {
                            targetView.setArcMode(true);
                            updateArcToggleUI.run();
                            exportToJson();
                        });
                    }

// ── Arc OFF
                    if (btnArcToggleOff != null) {
                        btnArcToggleOff.setOnClickListener(v1 -> {
                            targetView.setArcMode(false);
                            updateArcToggleUI.run();
                            exportToJson();
                        });
                    }

// ── Angle seekbar
                    if (sbArcAngle != null) {
                        sbArcAngle.setOnSeekBarChangeListener(
                                new android.widget.SeekBar.OnSeekBarChangeListener() {
                                    @Override
                                    public void onProgressChanged(
                                            android.widget.SeekBar s, int p, boolean fromUser) {
                                        if (!fromUser) return;
                                        float angle = Math.max(10, p);
                                        targetView.setArcAngle(angle);
                                        if (tvArcAngleVal != null)
                                            tvArcAngleVal.setText((int) angle + "°");
                                    }

                                    @Override
                                    public void onStartTrackingTouch(android.widget.SeekBar s) {
                                    }

                                    @Override
                                    public void onStopTrackingTouch(android.widget.SeekBar s) {
                                        exportToJson();
                                    }
                                });
                    }

// ── Angle +/-
                    if (btnArcAngleM != null && sbArcAngle != null) {
                        btnArcAngleM.setOnClickListener(v1 -> {
                            int nv = Math.max(10, sbArcAngle.getProgress() - 10);
                            sbArcAngle.setProgress(nv);
                            targetView.setArcAngle(nv);
                            if (tvArcAngleVal != null) tvArcAngleVal.setText(nv + "°");
                            exportToJson();
                        });
                    }
                    if (btnArcAngleP != null && sbArcAngle != null) {
                        btnArcAngleP.setOnClickListener(v1 -> {
                            int nv = Math.min(360, sbArcAngle.getProgress() + 10);
                            sbArcAngle.setProgress(nv);
                            targetView.setArcAngle(nv);
                            if (tvArcAngleVal != null) tvArcAngleVal.setText(nv + "°");
                            exportToJson();
                        });
                    }

// ── Radius seekbar
                    if (sbArcRadius != null) {
                        sbArcRadius.setOnSeekBarChangeListener(
                                new android.widget.SeekBar.OnSeekBarChangeListener() {
                                    @Override
                                    public void onProgressChanged(
                                            android.widget.SeekBar s, int p, boolean fromUser) {
                                        if (!fromUser) return;
                                        float rad = Math.max(50, p);
                                        targetView.setRadius(rad);
                                        if (tvArcRadiusVal != null)
                                            tvArcRadiusVal.setText((int) rad + "");
                                    }

                                    @Override
                                    public void onStartTrackingTouch(android.widget.SeekBar s) {
                                    }

                                    @Override
                                    public void onStopTrackingTouch(android.widget.SeekBar s) {
                                        exportToJson();
                                    }
                                });
                    }

// ── Radius +/-
                    if (btnArcRadiusM != null && sbArcRadius != null) {
                        btnArcRadiusM.setOnClickListener(v1 -> {
                            int nv = Math.max(50, sbArcRadius.getProgress() - 10);
                            sbArcRadius.setProgress(nv);
                            targetView.setRadius(nv);
                            if (tvArcRadiusVal != null) tvArcRadiusVal.setText(nv + "");
                            exportToJson();
                        });
                    }
                    if (btnArcRadiusP != null && sbArcRadius != null) {
                        btnArcRadiusP.setOnClickListener(v1 -> {
                            int nv = Math.min(500, sbArcRadius.getProgress() + 10);
                            sbArcRadius.setProgress(nv);
                            targetView.setRadius(nv);
                            if (tvArcRadiusVal != null) tvArcRadiusVal.setText(nv + "");
                            exportToJson();
                        });
                    }

// ── Direction
                    if (btnArcDirUp != null) {
                        btnArcDirUp.setOnClickListener(v1 -> {
                            targetView.setArcUp(true);
                            updateArcToggleUI.run();
                            exportToJson();
                        });
                    }
                    if (btnArcDirDown != null) {
                        btnArcDirDown.setOnClickListener(v1 -> {
                            targetView.setArcUp(false);
                            updateArcToggleUI.run();
                            exportToJson();
                        });
                    }

                }
            });
        }

        if (btnBold != null) {
            btnBold.setOnClickListener(v -> {
                boolean isBold = targetView.getTypeface() != null
                        && targetView.getTypeface().isBold();
                boolean isItalic2 = targetView.getTypeface() != null
                        && targetView.getTypeface().isItalic();
                int style = (!isBold && isItalic2) ? android.graphics.Typeface.BOLD_ITALIC
                        : (!isBold) ? android.graphics.Typeface.BOLD
                        : isItalic2 ? android.graphics.Typeface.ITALIC
                        : android.graphics.Typeface.NORMAL;
                targetView.setTypeface(android.graphics.Typeface.defaultFromStyle(style));
                updateStyleBtns.run();
                exportToJson();
            });
        }

        if (btnItalic != null) {
            btnItalic.setOnClickListener(v -> {
                boolean isBold = targetView.getTypeface() != null
                        && targetView.getTypeface().isBold();
                boolean isItalic2 = targetView.getTypeface() != null
                        && targetView.getTypeface().isItalic();
                int style = (isBold && !isItalic2) ? android.graphics.Typeface.BOLD_ITALIC
                        : (!isItalic2) ? android.graphics.Typeface.ITALIC
                        : isBold ? android.graphics.Typeface.BOLD
                        : android.graphics.Typeface.NORMAL;
                targetView.setTypeface(android.graphics.Typeface.defaultFromStyle(style));
                updateStyleBtns.run();
                exportToJson();
            });
        }

        if (btnUnderline != null) {
            btnUnderline.setOnClickListener(v -> {
                int flags = targetView.getPaintFlags();
                if ((flags & android.graphics.Paint.UNDERLINE_TEXT_FLAG) != 0) {
                    targetView.setPaintFlags(
                            flags & ~android.graphics.Paint.UNDERLINE_TEXT_FLAG);
                } else {
                    targetView.setPaintFlags(
                            flags | android.graphics.Paint.UNDERLINE_TEXT_FLAG);
                }
                updateStyleBtns.run();
                exportToJson();
            });
        }

        if (btnStrike != null) {
            btnStrike.setOnClickListener(v -> {
                int flags = targetView.getPaintFlags();
                if ((flags & android.graphics.Paint.STRIKE_THRU_TEXT_FLAG) != 0) {
                    targetView.setPaintFlags(
                            flags & ~android.graphics.Paint.STRIKE_THRU_TEXT_FLAG);
                } else {
                    targetView.setPaintFlags(
                            flags | android.graphics.Paint.STRIKE_THRU_TEXT_FLAG);
                }
                updateStyleBtns.run();
                exportToJson();
            });
        }

        if (btnFlipH != null) {
            btnFlipH.setOnClickListener(v -> {
                targetView.setScaleX(targetView.getScaleX() * -1);
                exportToJson();
            });
        }

        if (btnFlipV != null) {
            btnFlipV.setOnClickListener(v -> {
                targetView.setScaleY(targetView.getScaleY() * -1);
                exportToJson();
            });
        }

        // ════════════════════════════
        // TRANSFORM BUTTONS
        // ════════════════════════════
        TextView btnUpper = cv.findViewById(R.id.btn_transform_upper);
        TextView btnLower = cv.findViewById(R.id.btn_transform_lower);
        TextView btnTitle = cv.findViewById(R.id.btn_transform_title);
        TextView btnCopyStyle = cv.findViewById(R.id.btn_copy_style_all);
        TextView btnReset = cv.findViewById(R.id.btn_reset_style);

        if (btnUpper != null) {
            btnUpper.setOnClickListener(v -> {
                undoStack.push(targetView.getText().toString());
                redoStack.clear();
                targetView.setText(targetView.getText().toString().toUpperCase());
                exportToJson();
            });
        }
        if (btnLower != null) {
            btnLower.setOnClickListener(v -> {
                undoStack.push(targetView.getText().toString());
                redoStack.clear();
                targetView.setText(targetView.getText().toString().toLowerCase());
                exportToJson();
            });
        }
        if (btnTitle != null) {
            btnTitle.setOnClickListener(v -> {
                undoStack.push(targetView.getText().toString());
                redoStack.clear();
                String t = targetView.getText().toString();
                StringBuilder sb = new StringBuilder();
                boolean nextUp = true;
                for (char c : t.toCharArray()) {
                    if (Character.isWhitespace(c)) {
                        nextUp = true;
                        sb.append(c);
                    } else if (nextUp) {
                        sb.append(Character.toUpperCase(c));
                        nextUp = false;
                    } else {
                        sb.append(Character.toLowerCase(c));
                    }
                }
                targetView.setText(sb.toString());
                exportToJson();
            });
        }

        if (btnCopyStyle != null) {
            btnCopyStyle.setOnClickListener(v -> {
                new android.app.AlertDialog.Builder(this)
                        .setTitle("Style Copy to All Texts")
                        .setMessage("આ text ની styling (color, size, font, stroke) "
                                + "page ના બધા texts પર apply કરવી?")
                        .setPositiveButton("Apply All", (d, w) -> {
                            for (int i = 0; i < mainLayout.getChildCount(); i++) {
                                View child = mainLayout.getChildAt(i);
                                if (child instanceof StrokeTextView
                                        && child != targetView) {
                                    StrokeTextView other = (StrokeTextView) child;
                                    other.setTextColor(targetView.getCurrentTextColor());
                                    other.setTextSize(
                                            android.util.TypedValue.COMPLEX_UNIT_PX,
                                            targetView.getTextSize());
                                    other.setTypeface(targetView.getTypeface());
                                    other.setPaintFlags(targetView.getPaintFlags());
                                    other.setStrokeWidth(targetView.getStrokeWidth());
                                    other.setStrokeColor(targetView.getStrokeColor());
                                    other.setLetterSpacing(targetView.getLetterSpacing());
                                    other.setLineSpacing(0f,
                                            targetView.getLineSpacingMultiplier());
                                }
                            }
                            exportToJson();
                            Toast.makeText(this,
                                    "✅ Style copy to all texts!",
                                    Toast.LENGTH_SHORT).show();
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            });
        }

        if (btnReset != null) {
            btnReset.setOnClickListener(v -> {
                new android.app.AlertDialog.Builder(this)
                        .setTitle("Reset Style")
                        .setMessage("Default style restore કરવી?")
                        .setPositiveButton("Reset", (d, w) -> {
                            targetView.setTextColor(Color.BLACK);
                            targetView.setTextSize(20f);
                            targetView.setTypeface(
                                    android.graphics.Typeface.DEFAULT);
                            targetView.setPaintFlags(0);
                            targetView.setStrokeWidth(0f);
                            targetView.setStrokeColor(Color.BLACK);
                            targetView.setLetterSpacing(0f);
                            targetView.setLineSpacing(0f, 1.0f);
                            targetView.setAlpha(1.0f);
                            targetView.setScaleX(1.0f);
                            targetView.setScaleY(1.0f);
                            targetView.setRotation(0f);
                            targetView.setTextGradient(null);
                            targetView.setTag(R.id.tv_move_speed, null);
                            targetView.getPaint().clearShadowLayer();

                            GradientDrawable gd = new GradientDrawable();
                            gd.setColor(Color.TRANSPARENT);
                            gd.setStroke(0, Color.TRANSPARENT);
                            gd.setCornerRadius(8f);
                            targetView.setBackground(gd);
                            targetView.setTag(Color.TRANSPARENT);

                            exportToJson();
                            Toast.makeText(this, "✅ Reset!", Toast.LENGTH_SHORT).show();
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            });
        }

        // ════════════════════════════
        // TEXT ALIGNMENT
        // ════════════════════════════
        TextView btnAlignLeft = cv.findViewById(R.id.btn_text_align_left);
        TextView btnAlignCenter = cv.findViewById(R.id.btn_text_align_center);
        TextView btnAlignRight = cv.findViewById(R.id.btn_text_align_right);

        Runnable updateAlignUI = () -> {
            int activeColor = Color.parseColor("#1565C0");
            int inactiveColor = Color.parseColor("#E3F2FD");
            int gravity = targetView.getGravity()
                    & android.view.Gravity.HORIZONTAL_GRAVITY_MASK;

            if (btnAlignLeft != null) {
                boolean a = gravity == android.view.Gravity.LEFT
                        || gravity == android.view.Gravity.START;
                btnAlignLeft.setBackgroundColor(a ? activeColor : inactiveColor);
                btnAlignLeft.setTextColor(a ? Color.WHITE
                        : Color.parseColor("#1565C0"));
            }
            if (btnAlignCenter != null) {
                boolean a = gravity == android.view.Gravity.CENTER_HORIZONTAL
                        || gravity == android.view.Gravity.CENTER;
                btnAlignCenter.setBackgroundColor(a ? activeColor : inactiveColor);
                btnAlignCenter.setTextColor(a ? Color.WHITE
                        : Color.parseColor("#1565C0"));
            }
            if (btnAlignRight != null) {
                boolean a = gravity == android.view.Gravity.RIGHT
                        || gravity == android.view.Gravity.END;
                btnAlignRight.setBackgroundColor(a ? activeColor : inactiveColor);
                btnAlignRight.setTextColor(a ? Color.WHITE
                        : Color.parseColor("#1565C0"));
            }
        };
        updateAlignUI.run();

        if (btnAlignLeft != null)
            btnAlignLeft.setOnClickListener(v -> {
                targetView.setGravity(
                        android.view.Gravity.START | android.view.Gravity.CENTER_VERTICAL);
                updateAlignUI.run();
                exportToJson();
            });
        if (btnAlignCenter != null)
            btnAlignCenter.setOnClickListener(v -> {
                targetView.setGravity(android.view.Gravity.CENTER);
                updateAlignUI.run();
                exportToJson();
            });
        if (btnAlignRight != null)
            btnAlignRight.setOnClickListener(v -> {
                targetView.setGravity(
                        android.view.Gravity.END | android.view.Gravity.CENTER_VERTICAL);
                updateAlignUI.run();
                exportToJson();
            });

        // ════════════════════════════
        // PADDING SEEKBAR
        // ════════════════════════════
        TextView tvPaddingLabel = cv.findViewById(R.id.tv_text_padding_label);
        android.widget.SeekBar seekPadding = cv.findViewById(R.id.seek_text_padding);
        if (seekPadding != null) {
            int currentPadding = Math.max(0,
                    targetView.getPaddingLeft()
                            - (int) Math.ceil(targetView.getStrokeWidth()) - 4);
            seekPadding.setMax(80);
            seekPadding.setProgress(Math.min(80, currentPadding));
            if (tvPaddingLabel != null)
                tvPaddingLabel.setText(currentPadding + "px");

            seekPadding.setOnSeekBarChangeListener(
                    new android.widget.SeekBar.OnSeekBarChangeListener() {
                        @Override
                        public void onProgressChanged(android.widget.SeekBar s,
                                                      int p, boolean fromUser) {
                            if (!fromUser) return;
                            int strokeExtra = (int) Math.ceil(
                                    targetView.getStrokeWidth()) + 4;
                            int total = strokeExtra + p;
                            targetView.setPadding(total, total, total, total);
                            if (tvPaddingLabel != null)
                                tvPaddingLabel.setText(p + "px");
                            targetView.requestLayout();
                        }

                        @Override
                        public void onStartTrackingTouch(
                                android.widget.SeekBar s) {
                        }

                        @Override
                        public void onStopTrackingTouch(
                                android.widget.SeekBar s) {
                            exportToJson();
                        }
                    });
        }
        TextView btnPadM = cv.findViewById(R.id.btn_text_pad_minus);
        TextView btnPadP = cv.findViewById(R.id.btn_text_pad_plus);
        if (btnPadM != null && seekPadding != null) {
            btnPadM.setOnClickListener(v -> {
                int nv = Math.max(0, seekPadding.getProgress() - 2);
                seekPadding.setProgress(nv);
                int se = (int) Math.ceil(targetView.getStrokeWidth()) + 4;
                targetView.setPadding(se + nv, se + nv, se + nv, se + nv);
                if (tvPaddingLabel != null) tvPaddingLabel.setText(nv + "px");
                targetView.requestLayout();
                exportToJson();
            });
        }
        if (btnPadP != null && seekPadding != null) {
            btnPadP.setOnClickListener(v -> {
                int nv = Math.min(80, seekPadding.getProgress() + 2);
                seekPadding.setProgress(nv);
                int se = (int) Math.ceil(targetView.getStrokeWidth()) + 4;
                targetView.setPadding(se + nv, se + nv, se + nv, se + nv);
                if (tvPaddingLabel != null) tvPaddingLabel.setText(nv + "px");
                targetView.requestLayout();
                exportToJson();
            });
        }

        // ════════════════════════════
        // LINE SPACING
        // ════════════════════════════
        TextView tvLineLabel = cv.findViewById(R.id.tv_line_spacing_label);
        android.widget.SeekBar seekLine = cv.findViewById(R.id.seek_line_spacing);
        if (seekLine != null) {
            float curMult = targetView.getLineSpacingMultiplier();
            int lineP = Math.round((curMult - 1.0f) * 10f);
            seekLine.setMax(40);
            seekLine.setProgress(Math.max(0, Math.min(40, lineP)));
            if (tvLineLabel != null)
                tvLineLabel.setText(String.format("%.1f", curMult) + "x");

            seekLine.setOnSeekBarChangeListener(
                    new android.widget.SeekBar.OnSeekBarChangeListener() {
                        @Override
                        public void onProgressChanged(android.widget.SeekBar s,
                                                      int p, boolean fromUser) {
                            if (!fromUser) return;
                            float m = 1.0f + p * 0.1f;
                            targetView.setLineSpacing(0f, m);
                            if (tvLineLabel != null)
                                tvLineLabel.setText(String.format("%.1f", m) + "x");
                        }

                        @Override
                        public void onStartTrackingTouch(
                                android.widget.SeekBar s) {
                        }

                        @Override
                        public void onStopTrackingTouch(
                                android.widget.SeekBar s) {
                            exportToJson();
                        }
                    });
        }
        TextView btnLineM = cv.findViewById(R.id.btn_line_minus);
        TextView btnLineP = cv.findViewById(R.id.btn_line_plus);
        if (btnLineM != null && seekLine != null) {
            btnLineM.setOnClickListener(v -> {
                int nv = Math.max(0, seekLine.getProgress() - 1);
                seekLine.setProgress(nv);
                float m = 1.0f + nv * 0.1f;
                targetView.setLineSpacing(0f, m);
                if (tvLineLabel != null)
                    tvLineLabel.setText(String.format("%.1f", m) + "x");
                exportToJson();
            });
        }
        if (btnLineP != null && seekLine != null) {
            btnLineP.setOnClickListener(v -> {
                int nv = Math.min(40, seekLine.getProgress() + 1);
                seekLine.setProgress(nv);
                float m = 1.0f + nv * 0.1f;
                targetView.setLineSpacing(0f, m);
                if (tvLineLabel != null)
                    tvLineLabel.setText(String.format("%.1f", m) + "x");
                exportToJson();
            });
        }

        // ════════════════════════════
        // LETTER SPACING
        // ════════════════════════════
        TextView tvLetterLabel = cv.findViewById(R.id.tv_letter_spacing_label);
        android.widget.SeekBar seekLetter = cv.findViewById(R.id.seek_letter_spacing);
        if (seekLetter != null) {
            float curLS = targetView.getLetterSpacing();
            seekLetter.setMax(40);
            seekLetter.setProgress(Math.round(curLS * 20f));
            if (tvLetterLabel != null)
                tvLetterLabel.setText(String.format("%.2f", curLS));

            seekLetter.setOnSeekBarChangeListener(
                    new android.widget.SeekBar.OnSeekBarChangeListener() {
                        @Override
                        public void onProgressChanged(android.widget.SeekBar s,
                                                      int p, boolean fromUser) {
                            if (!fromUser) return;
                            float ls = p * 0.05f;
                            targetView.setLetterSpacing(ls);
                            if (tvLetterLabel != null)
                                tvLetterLabel.setText(String.format("%.2f", ls));
                        }

                        @Override
                        public void onStartTrackingTouch(
                                android.widget.SeekBar s) {
                        }

                        @Override
                        public void onStopTrackingTouch(
                                android.widget.SeekBar s) {
                            exportToJson();
                        }
                    });
        }
        TextView btnLetM = cv.findViewById(R.id.btn_letter_minus);
        TextView btnLetP = cv.findViewById(R.id.btn_letter_plus);
        if (btnLetM != null && seekLetter != null) {
            btnLetM.setOnClickListener(v -> {
                int nv = Math.max(0, seekLetter.getProgress() - 1);
                seekLetter.setProgress(nv);
                float ls = nv * 0.05f;
                targetView.setLetterSpacing(ls);
                if (tvLetterLabel != null)
                    tvLetterLabel.setText(String.format("%.2f", ls));
                exportToJson();
            });
        }
        if (btnLetP != null && seekLetter != null) {
            btnLetP.setOnClickListener(v -> {
                int nv = Math.min(40, seekLetter.getProgress() + 1);
                seekLetter.setProgress(nv);
                float ls = nv * 0.05f;
                targetView.setLetterSpacing(ls);
                if (tvLetterLabel != null)
                    tvLetterLabel.setText(String.format("%.2f", ls));
                exportToJson();
            });
        }

        // ════════════════════════════
        // ROTATION SEEKBAR
        // ════════════════════════════
        TextView tvRotLabel = cv.findViewById(R.id.tv_rotation_label);
        android.widget.SeekBar seekRot = cv.findViewById(R.id.seek_rotation);
        if (seekRot != null) {
            float curRot = targetView.getRotation();
            int rotP = (int) (curRot + 180f);
            seekRot.setMax(360);
            seekRot.setProgress(Math.max(0, Math.min(360, rotP)));
            if (tvRotLabel != null)
                tvRotLabel.setText((int) curRot + "°");

            seekRot.setOnSeekBarChangeListener(
                    new android.widget.SeekBar.OnSeekBarChangeListener() {
                        @Override
                        public void onProgressChanged(android.widget.SeekBar s,
                                                      int p, boolean fromUser) {
                            if (!fromUser) return;
                            float rot = p - 180f;
                            targetView.setRotation(rot);
                            if (tvRotLabel != null)
                                tvRotLabel.setText((int) rot + "°");
                        }

                        @Override
                        public void onStartTrackingTouch(
                                android.widget.SeekBar s) {
                        }

                        @Override
                        public void onStopTrackingTouch(
                                android.widget.SeekBar s) {
                            exportToJson();
                        }
                    });
        }
        TextView btnRotM = cv.findViewById(R.id.btn_rotation_minus);
        TextView btnRotP = cv.findViewById(R.id.btn_rotation_plus);
        if (btnRotM != null && seekRot != null) {
            btnRotM.setOnClickListener(v -> {
                int nv = Math.max(0, seekRot.getProgress() - 5);
                seekRot.setProgress(nv);
                float rot = nv - 180f;
                targetView.setRotation(rot);
                if (tvRotLabel != null) tvRotLabel.setText((int) rot + "°");
                exportToJson();
            });
        }
        if (btnRotP != null && seekRot != null) {
            btnRotP.setOnClickListener(v -> {
                int nv = Math.min(360, seekRot.getProgress() + 5);
                seekRot.setProgress(nv);
                float rot = nv - 180f;
                targetView.setRotation(rot);
                if (tvRotLabel != null) tvRotLabel.setText((int) rot + "°");
                exportToJson();
            });
        }

        // ════════════════════════════
        // OPACITY SEEKBAR
        // ════════════════════════════
        TextView tvOpacityLabel = cv.findViewById(R.id.tv_opacity_label);
        android.widget.SeekBar seekOpacity = cv.findViewById(R.id.seek_opacity);
        if (seekOpacity != null) {
            int curOpacity = Math.round(targetView.getAlpha() * 100f);
            seekOpacity.setMax(100);
            seekOpacity.setProgress(curOpacity);
            if (tvOpacityLabel != null)
                tvOpacityLabel.setText(curOpacity + "%");

            seekOpacity.setOnSeekBarChangeListener(
                    new android.widget.SeekBar.OnSeekBarChangeListener() {
                        @Override
                        public void onProgressChanged(android.widget.SeekBar s,
                                                      int p, boolean fromUser) {
                            if (!fromUser) return;
                            targetView.setAlpha(p / 100f);
                            if (tvOpacityLabel != null)
                                tvOpacityLabel.setText(p + "%");
                        }

                        @Override
                        public void onStartTrackingTouch(
                                android.widget.SeekBar s) {
                        }

                        @Override
                        public void onStopTrackingTouch(
                                android.widget.SeekBar s) {
                            exportToJson();
                        }
                    });
        }
        TextView btnOpM = cv.findViewById(R.id.btn_opacity_minus);
        TextView btnOpP = cv.findViewById(R.id.btn_opacity_plus);
        if (btnOpM != null && seekOpacity != null) {
            btnOpM.setOnClickListener(v -> {
                int nv = Math.max(0, seekOpacity.getProgress() - 5);
                seekOpacity.setProgress(nv);
                targetView.setAlpha(nv / 100f);
                if (tvOpacityLabel != null) tvOpacityLabel.setText(nv + "%");
                exportToJson();
            });
        }
        if (btnOpP != null && seekOpacity != null) {
            btnOpP.setOnClickListener(v -> {
                int nv = Math.min(100, seekOpacity.getProgress() + 5);
                seekOpacity.setProgress(nv);
                targetView.setAlpha(nv / 100f);
                if (tvOpacityLabel != null) tvOpacityLabel.setText(nv + "%");
                exportToJson();
            });
        }

        // ════════════════════════════
        // SCALE X / Y
        // ════════════════════════════
        android.widget.SeekBar seekScaleX = cv.findViewById(R.id.seek_scale_x);
        android.widget.SeekBar seekScaleY = cv.findViewById(R.id.seek_scale_y);
        TextView tvScaleX = cv.findViewById(R.id.tv_scale_x_label);
        TextView tvScaleY = cv.findViewById(R.id.tv_scale_y_label);

        if (seekScaleX != null) {
            float curSX = Math.abs(targetView.getScaleX());
            seekScaleX.setMax(40);
            seekScaleX.setProgress(Math.round(curSX * 10f));
            if (tvScaleX != null) tvScaleX.setText(
                    String.format("%.1f", curSX));

            seekScaleX.setOnSeekBarChangeListener(
                    new android.widget.SeekBar.OnSeekBarChangeListener() {
                        @Override
                        public void onProgressChanged(android.widget.SeekBar s,
                                                      int p, boolean fromUser) {
                            if (!fromUser) return;
                            float sc = Math.max(0.1f, p * 0.1f);
                            // Preserve flip direction
                            float sign = targetView.getScaleX() < 0 ? -1f : 1f;
                            targetView.setScaleX(sc * sign);
                            if (tvScaleX != null)
                                tvScaleX.setText(String.format("%.1f", sc));
                        }

                        @Override
                        public void onStartTrackingTouch(
                                android.widget.SeekBar s) {
                        }

                        @Override
                        public void onStopTrackingTouch(
                                android.widget.SeekBar s) {
                            exportToJson();
                        }
                    });
        }

        if (seekScaleY != null) {
            float curSY = Math.abs(targetView.getScaleY());
            seekScaleY.setMax(40);
            seekScaleY.setProgress(Math.round(curSY * 10f));
            if (tvScaleY != null) tvScaleY.setText(
                    String.format("%.1f", curSY));

            seekScaleY.setOnSeekBarChangeListener(
                    new android.widget.SeekBar.OnSeekBarChangeListener() {
                        @Override
                        public void onProgressChanged(android.widget.SeekBar s,
                                                      int p, boolean fromUser) {
                            if (!fromUser) return;
                            float sc = Math.max(0.1f, p * 0.1f);
                            float sign = targetView.getScaleY() < 0 ? -1f : 1f;
                            targetView.setScaleY(sc * sign);
                            if (tvScaleY != null)
                                tvScaleY.setText(String.format("%.1f", sc));
                        }

                        @Override
                        public void onStartTrackingTouch(
                                android.widget.SeekBar s) {
                        }

                        @Override
                        public void onStopTrackingTouch(
                                android.widget.SeekBar s) {
                            exportToJson();
                        }
                    });
        }

        // ════════════════════════════
        // SHADOW
        // ════════════════════════════
        final int[] shadowColor = {Color.parseColor("#80000000")};
        android.widget.SeekBar seekShadowR = cv.findViewById(R.id.seek_shadow_radius);
        android.widget.SeekBar seekShadowDX = cv.findViewById(R.id.seek_shadow_dx);
        android.widget.SeekBar seekShadowDY = cv.findViewById(R.id.seek_shadow_dy);
        TextView btnShadowColor = cv.findViewById(R.id.btn_shadow_color);
        TextView btnShadowRemove = cv.findViewById(R.id.btn_shadow_remove);

        Runnable applyShadow = () -> {
            if (seekShadowR == null) return;
            float r = seekShadowR.getProgress();
            float dx = seekShadowDX != null ? (float) seekShadowDX.getProgress() : 3f;
            float dy = seekShadowDY != null ? (float) seekShadowDY.getProgress() : 3f;

            // ✅ Shadow color ખાલી હોય તો default
            int sColor = shadowColor[0] == 0
                    ? Color.parseColor("#80000000")
                    : shadowColor[0];

            if (r > 0) {
                // ✅ માત્ર shadow set — background touch નહીં
                targetView.setShadowLayer(r, dx, dy, sColor);
            } else {
                targetView.setShadowLayer(0, 0, 0, Color.TRANSPARENT);
            }

            // ✅ Background color change નહીં
            targetView.invalidate();
        };

        android.widget.SeekBar.OnSeekBarChangeListener shadowListener =
                new android.widget.SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(android.widget.SeekBar s,
                                                  int p, boolean fromUser) {
                        if (!fromUser) return;
                        applyShadow.run();
                    }

                    @Override
                    public void onStartTrackingTouch(
                            android.widget.SeekBar s) {
                    }

                    @Override
                    public void onStopTrackingTouch(
                            android.widget.SeekBar s) {
                    }
                };

        if (seekShadowR != null) seekShadowR.setOnSeekBarChangeListener(shadowListener);
        if (seekShadowDX != null) seekShadowDX.setOnSeekBarChangeListener(shadowListener);
        if (seekShadowDY != null) seekShadowDY.setOnSeekBarChangeListener(shadowListener);

        if (btnShadowColor != null) {
            // ── Initial color show
            btnShadowColor.setBackgroundColor(shadowColor[0]);

            btnShadowColor.setOnClickListener(v -> {
                // ── Shadow color ની current value
                int currentShadowColor = shadowColor[0] == 0
                        ? Color.parseColor("#80000000")
                        : shadowColor[0];

                showColorPickerPopup(currentShadowColor, color -> {
                                // ✅ માત્ર shadow color update
                                shadowColor[0] = color;
                                btnShadowColor.setBackgroundColor(color);

                                // ✅ Shadow apply — background color change નહીં
                                if (seekShadowR != null && seekShadowR.getProgress() > 0) {
                                    applyShadow.run();
                                } else {
                                    if (seekShadowR != null) {
                                        seekShadowR.setProgress(5);
                                    }
                                    applyShadow.run();
                                }
                                exportToJson();
                });
            });
        }

        if (btnShadowRemove != null) {
            btnShadowRemove.setOnClickListener(v -> {
                targetView.setShadowLayer(0, 0, 0, Color.TRANSPARENT);
                if (seekShadowR != null) seekShadowR.setProgress(0);
                targetView.invalidate();
                exportToJson();
            });
        }

        // ════════════════════════════
        // CORNER RADIUS
        // ════════════════════════════
        android.widget.SeekBar seekCorner = cv.findViewById(R.id.seek_corner_radius);
        TextView tvCornerLabel = cv.findViewById(R.id.tv_corner_label);

        if (seekCorner != null) {
            seekCorner.setMax(60);
            seekCorner.setProgress(8);
            if (tvCornerLabel != null) tvCornerLabel.setText("8dp");

            seekCorner.setOnSeekBarChangeListener(
                    new android.widget.SeekBar.OnSeekBarChangeListener() {
                        @Override
                        public void onProgressChanged(android.widget.SeekBar s,
                                                      int p, boolean fromUser) {
                            if (!fromUser) return;
                            if (tvCornerLabel != null) tvCornerLabel.setText(p + "dp");
                            android.graphics.drawable.Drawable bg = targetView.getBackground();
                            if (bg instanceof GradientDrawable) {
                                ((GradientDrawable) bg).setCornerRadius(
                                        dpToPx(p));
                            } else {
                                GradientDrawable gd = new GradientDrawable();
                                gd.setColor(getStoredBackgroundColor(targetView));
                                gd.setCornerRadius(dpToPx(p));
                                targetView.setBackground(gd);
                            }
                        }

                        @Override
                        public void onStartTrackingTouch(
                                android.widget.SeekBar s) {
                        }

                        @Override
                        public void onStopTrackingTouch(
                                android.widget.SeekBar s) {
                            exportToJson();
                        }
                    });
        }

        // ════════════════════════════
        // HIGHLIGHT COLOR
        // ════════════════════════════
        TextView btnHighlight = cv.findViewById(R.id.btn_highlight_color);
        if (btnHighlight != null) {
            btnHighlight.setOnClickListener(v -> {
                String[] colors = {"None", "Yellow", "Cyan",
                        "Green", "Pink", "Orange"};
                int[] colorVals = {
                        Color.TRANSPARENT,
                        Color.parseColor("#FFFF00"),
                        Color.parseColor("#00FFFF"),
                        Color.parseColor("#90EE90"),
                        Color.parseColor("#FFB6C1"),
                        Color.parseColor("#FFA500")
                };
                new android.app.AlertDialog.Builder(this)
                        .setTitle("Highlight Color")
                        .setItems(colors, (d, w) -> {
                            GradientDrawable gd = new GradientDrawable();
                            gd.setColor(colorVals[w]);
                            Object borderTag = targetView.getTag(
                                    R.id.btn_add_sticker);
                            int borderStyle = borderTag instanceof Integer
                                    ? (int) borderTag : 0;
                            applyBorderStyle(gd, borderStyle);
                            targetView.setBackground(gd);
                            targetView.setTag(colorVals[w]);
                            exportToJson();
                        })
                        .show();
            });
        }

        // ════════════════════════════
        // UNDO / REDO
        // ════════════════════════════
        TextView btnUndo = cv.findViewById(R.id.btn_undo);
        TextView btnRedo = cv.findViewById(R.id.btn_redo);

        if (btnUndo != null) {
            btnUndo.setOnClickListener(v -> {
                if (undoStack.size() > 1) {
                    redoStack.push(targetView.getText().toString());
                    undoStack.pop();
                    targetView.setText(undoStack.peek());
                    exportToJson();
                    Toast.makeText(this, "↩ Undo",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Nothing to undo",
                            Toast.LENGTH_SHORT).show();
                }
            });
        }

        if (btnRedo != null) {
            btnRedo.setOnClickListener(v -> {
                if (!redoStack.isEmpty()) {
                    undoStack.push(redoStack.pop());
                    targetView.setText(undoStack.peek());
                    exportToJson();
                    Toast.makeText(this, "↪ Redo",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Nothing to redo",
                            Toast.LENGTH_SHORT).show();
                }
            });
        }

        // ════════════════════════════
        // DUPLICATE
        // ════════════════════════════
        View btnDuplicate = cv.findViewById(R.id.btn_duplicate_text);
        if (btnDuplicate != null) {
            btnDuplicate.setOnClickListener(v -> {
                StrokeTextView copy = new StrokeTextView(this);
                copy.setText(targetView.getText());
                copy.setTextColor(targetView.getCurrentTextColor());
                copy.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX,
                        targetView.getTextSize());
                copy.setTypeface(targetView.getTypeface());
                copy.setPaintFlags(targetView.getPaintFlags());
                copy.setLetterSpacing(targetView.getLetterSpacing());
                copy.setLineSpacing(0f, targetView.getLineSpacingMultiplier());
                copy.setStrokeWidth(targetView.getStrokeWidth());
                copy.setStrokeColor(targetView.getStrokeColor());
                copy.setAlpha(targetView.getAlpha());
                copy.setRotation(targetView.getRotation());
                copy.setScaleX(targetView.getScaleX());
                copy.setScaleY(targetView.getScaleY());
                copy.setGravity(targetView.getGravity());
                copy.setPadding(targetView.getPaddingLeft(),
                        targetView.getPaddingTop(),
                        targetView.getPaddingRight(),
                        targetView.getPaddingBottom());

                int bgColor = getStoredBackgroundColor(targetView);
                GradientDrawable gd = new GradientDrawable();
                gd.setColor(bgColor);
                gd.setStroke(0, Color.TRANSPARENT);
                gd.setCornerRadius(8f);
                copy.setBackground(gd);
                copy.setTag(bgColor);

                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT);
                copy.setLayoutParams(lp);

                applyTouchListener(copy);
                mainLayout.addView(copy);

                copy.post(() -> {
                    copy.setX(targetView.getX() + 30);
                    copy.setY(targetView.getY() + 30);
                });

                exportToJson();
                Toast.makeText(this, "✅ Duplicate!", Toast.LENGTH_SHORT).show();
            });
        }

        // ════════════════════════════
        // QR CODE GENERATE
        // ════════════════════════════
        View btnQR = cv.findViewById(R.id.btn_qr_generate);
        if (btnQR != null) {
            btnQR.setOnClickListener(v -> {
                String text = targetView.getText().toString().trim();
                if (text.isEmpty()) {
                    Toast.makeText(this, "Text empty — QR generate ન થઈ શકે",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                generateQRCode(text);
            });
        }

        // ════════════════════════════
        // SIZE SEEKBAR
        // ════════════════════════════
        android.widget.SeekBar seekSize = cv.findViewById(R.id.seek_sel_size);
        TextView tvLbl = cv.findViewById(R.id.tv_sel_size_label);

        if (seekSize != null && !isLocked) {
            seekSize.setOnSeekBarChangeListener(
                    new android.widget.SeekBar.OnSeekBarChangeListener() {
                        @Override
                        public void onProgressChanged(android.widget.SeekBar s,
                                                      int p, boolean fromUser) {
                            if (!fromUser) return;
                            applySelSizeChange(targetView, p, tvLbl);
                        }

                        @Override
                        public void onStartTrackingTouch(
                                android.widget.SeekBar s) {
                        }

                        @Override
                        public void onStopTrackingTouch(
                                android.widget.SeekBar s) {
                            exportToJson();
                        }
                    });
        }

        bindSizeButtons(cv, targetView, seekSize, tvLbl);
        bindMoveButtons(cv, targetView);
        bindLayerButtons(cv, targetView);
        bindAlignButtons(cv, targetView);
        bindToggleRows(cv);

        // ── Font / Stroke / Gradient / Border / BG Image
        View btnFont = cv.findViewById(R.id.btn_sel_font);
        if (btnFont != null)
            btnFont.setOnClickListener(v -> showFontPickerDialog(targetView));

        View btnGradient = cv.findViewById(R.id.btn_pop_gradient);
        if (btnGradient != null)
            btnGradient.setOnClickListener(v -> showGradientPopupWindow(targetView));

        View btnStroke2 = cv.findViewById(R.id.btn_sel_stroke);
        if (btnStroke2 != null)
            btnStroke2.setOnClickListener(v -> showStrokeDialog(targetView));
        View btnBorder2 = cv.findViewById(R.id.btn_sel_border);
        if (btnBorder2 != null)
            btnBorder2.setOnClickListener(v -> {

                // ── Current border values read karo tag thi
                final int[] borderColorArr = {Color.BLACK};
                final int[] borderStyleArr = {0};
                int currentWidth = 2;
                int currentCorner = 8;

                Object borderInfoTag = targetView.getTag(R.id.tv_border_info);
                if (borderInfoTag instanceof int[]) {
                    int[] info = (int[]) borderInfoTag;
                    currentWidth  = info[0];           // width
                    borderColorArr[0] = info[1];       // color
                    currentCorner = info[2];           // corner
                    borderStyleArr[0] = info[3];       // style
                }

                final int finalCurrentWidth  = currentWidth;
                final int finalCurrentCorner = currentCorner;


                View borderPopupView = LayoutInflater.from(this)
                        .inflate(R.layout.popup_border_controls, null);

                android.util.DisplayMetrics dm = new android.util.DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(dm);
                int screenWidth = dm.widthPixels;

                PopupWindow borderPopup = new PopupWindow(
                        borderPopupView,
                        screenWidth,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        true
                );
                borderPopup.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                borderPopup.setElevation(16f);
                borderPopup.setOutsideTouchable(true);

                View rootView = getWindow().getDecorView().getRootView();
                android.util.DisplayMetrics dm2 = new android.util.DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(dm2);
                borderPopup.showAtLocation(rootView, Gravity.TOP | Gravity.LEFT, 0, dm2.heightPixels / 4);

                // ── Drag (smooth)
                View borderDrag = borderPopupView.findViewById(R.id.drag_handle_border);
                final float[] bLastX = {0}, bLastY = {0};
                final int[] bPopX = {0}, bPopY = {0};
                if (borderDrag != null) {
                    borderDrag.setOnTouchListener((bv, event) -> {
                        switch (event.getAction()) {
                            case MotionEvent.ACTION_DOWN:
                                bLastX[0] = event.getRawX();
                                bLastY[0] = event.getRawY();
                                int[] bloc = new int[2];
                                borderPopupView.getLocationOnScreen(bloc);
                                bPopX[0] = bloc[0];
                                bPopY[0] = bloc[1];
                                break;
                            case MotionEvent.ACTION_MOVE:
                                float dx = event.getRawX() - bLastX[0];
                                float dy = event.getRawY() - bLastY[0];
                                bPopX[0] += (int) dx;
                                bPopY[0] += (int) dy;
                                borderPopup.update(bPopX[0], bPopY[0], -1, -1);
                                bLastX[0] = event.getRawX();
                                bLastY[0] = event.getRawY();
                                break;
                        }
                        return true;
                    });
                }

                // ── Views
                TextView btnBorderClose = borderPopupView.findViewById(R.id.btn_border_close);
                SeekBar sbBorderWidth = borderPopupView.findViewById(R.id.sb_border_width);
                TextView tvBorderWidth = borderPopupView.findViewById(R.id.tv_border_width_val);
                TextView btnBorderColor = borderPopupView.findViewById(R.id.btn_border_color);
                SeekBar sbCornerRadius = borderPopupView.findViewById(R.id.sb_border_corner);
                TextView tvCornerVal = borderPopupView.findViewById(R.id.tv_border_corner_val);
                TextView btnBorderRemove = borderPopupView.findViewById(R.id.btn_border_remove);

                // Style buttons
                TextView btnStyleSolid = borderPopupView.findViewById(R.id.btn_border_solid);
                TextView btnStyleDash = borderPopupView.findViewById(R.id.btn_border_dash);
                TextView btnStyleDot = borderPopupView.findViewById(R.id.btn_border_dot);
                TextView btnStyleDouble = borderPopupView.findViewById(R.id.btn_border_double);

                // ── Current values already set above from tv_border_info tag
                // Existing background thi values read karo (already done above)

                if (sbBorderWidth != null) {
                    sbBorderWidth.setMax(20);
                    sbBorderWidth.setProgress(currentWidth);
                    if (tvBorderWidth != null) tvBorderWidth.setText(currentWidth + "dp");
                }
                if (sbCornerRadius != null) {
                    sbCornerRadius.setMax(60);
                    sbCornerRadius.setProgress(currentCorner);
                    if (tvCornerVal != null) tvCornerVal.setText(currentCorner + "dp");
                }

                // ── Apply border helper
                Runnable applyBorder = () -> {
                    int width = sbBorderWidth != null ? sbBorderWidth.getProgress() : 2;
                    int corner = sbCornerRadius != null ? sbCornerRadius.getProgress() : 8;
                    int color = borderColorArr[0];

                    GradientDrawable gd = new GradientDrawable();
                    gd.setColor(getStoredBackgroundColor(targetView));
                    gd.setCornerRadius(dpToPx(corner));

                    switch (borderStyleArr[0]) {
                        case 0: // Solid
                            gd.setStroke(dpToPx(width), color);
                            break;
                        case 1: // Dash
                            gd.setStroke(dpToPx(width), color, dpToPx(8), dpToPx(4));
                            break;
                        case 2: // Dot
                            gd.setStroke(dpToPx(width), color, dpToPx(2), dpToPx(4));
                            break;
                        case 3: // Double — 2 layer effect
                            gd.setStroke(dpToPx(width + 2), color);
                            break;
                    }

                    targetView.setBackground(gd);
                    targetView.setTag(R.id.btn_add_sticker, borderStyleArr[0]);

                    // ✅ Border info tag ma save karo jethi deselect pachhi pan restore thay
                    targetView.setTag(R.id.tv_border_info,
                            new int[]{width, color, corner, borderStyleArr[0]});

                    exportToJson();
                };

                // ── Style button highlight helper
                TextView[] styleBtns = {btnStyleSolid, btnStyleDash, btnStyleDot, btnStyleDouble};
                Runnable updateStyleUI = () -> {
                    for (int i = 0; i < styleBtns.length; i++) {
                        if (styleBtns[i] == null) continue;
                        boolean active = borderStyleArr[0] == i;
                        styleBtns[i].setBackgroundColor(active
                                ? Color.parseColor("#1565C0")
                                : Color.parseColor("#E3F2FD"));
                        styleBtns[i].setTextColor(active
                                ? Color.WHITE
                                : Color.parseColor("#1565C0"));
                    }
                };
                updateStyleUI.run();

                // ── Width seekbar
                if (sbBorderWidth != null) {
                    sbBorderWidth.setOnSeekBarChangeListener(
                            new SeekBar.OnSeekBarChangeListener() {
                                @Override
                                public void onProgressChanged(SeekBar s, int p, boolean u) {
                                    if (!u) return;
                                    int w = Math.max(1, p);
                                    if (tvBorderWidth != null) tvBorderWidth.setText(w + "dp");
                                    applyBorder.run();
                                }

                                @Override
                                public void onStartTrackingTouch(SeekBar s) {
                                }

                                @Override
                                public void onStopTrackingTouch(SeekBar s) {
                                }
                            });
                }

                // ── Corner seekbar
                if (sbCornerRadius != null) {
                    sbCornerRadius.setOnSeekBarChangeListener(
                            new SeekBar.OnSeekBarChangeListener() {
                                @Override
                                public void onProgressChanged(SeekBar s, int p, boolean u) {
                                    if (!u) return;
                                    if (tvCornerVal != null) tvCornerVal.setText(p + "dp");
                                    applyBorder.run();
                                }

                                @Override
                                public void onStartTrackingTouch(SeekBar s) {
                                }

                                @Override
                                public void onStopTrackingTouch(SeekBar s) {
                                }
                            });
                }

                // ── Border color
                if (btnBorderColor != null) {
                    btnBorderColor.setBackgroundColor(borderColorArr[0]);
                    btnBorderColor.setOnClickListener(bv -> {
                        showColorPickerPopup(
                        borderColorArr[0],
                        color -> {
                            borderColorArr[0] = color;
                                        btnBorderColor.setBackgroundColor(color);
                                        applyBorder.run();
                        });
                    });
                }

                // ── Style buttons
                if (btnStyleSolid != null) btnStyleSolid.setOnClickListener(bv -> {
                    borderStyleArr[0] = 0;
                    updateStyleUI.run();
                    applyBorder.run();
                });
                if (btnStyleDash != null) btnStyleDash.setOnClickListener(bv -> {
                    borderStyleArr[0] = 1;
                    updateStyleUI.run();
                    applyBorder.run();
                });
                if (btnStyleDot != null) btnStyleDot.setOnClickListener(bv -> {
                    borderStyleArr[0] = 2;
                    updateStyleUI.run();
                    applyBorder.run();
                });
                if (btnStyleDouble != null) btnStyleDouble.setOnClickListener(bv -> {
                    borderStyleArr[0] = 3;
                    updateStyleUI.run();
                    applyBorder.run();
                });

                // ── Remove border
                if (btnBorderRemove != null) {
                    btnBorderRemove.setOnClickListener(bv -> {
                        GradientDrawable gd = new GradientDrawable();
                        gd.setColor(getStoredBackgroundColor(targetView));
                        gd.setStroke(0, Color.TRANSPARENT);
                        gd.setCornerRadius(0f);
                        targetView.setBackground(gd);
                        // ✅ Border info tag clear karo — jethi restore na thay
                        targetView.setTag(R.id.tv_border_info, null);
                        targetView.setTag(R.id.btn_add_sticker, 0);
                        exportToJson();
                        borderPopup.dismiss();
                    });
                }

                // ── Close
                if (btnBorderClose != null) {
                    btnBorderClose.setOnClickListener(bv -> borderPopup.dismiss());
                }

            });

        View btnBgImage1 = cv.findViewById(R.id.btn_sel_bg_image);

        btnBgImage1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap bitmap = BitmapFactory.decodeResource(
                        getResources(), R.drawable.my_image2
                );
                targetView.setImageBitmap(bitmap);
            }
        });
      /*  if (btnBgImage1 != null)
            btnBgImage1.setOnClickListener(v -> showTextBgImageDialog(targetView));*/
        // ── BG Color
        View btnBgColor = cv.findViewById(R.id.btn_sel_bg_color);
        if (btnBgColor != null) {
            btnBgColor.setOnClickListener(v ->
                    showColorPickerPopup(
                        getStoredBackgroundColor(targetView),
                        color -> {
                            GradientDrawable gd = new GradientDrawable();
                                    gd.setColor(color);
                                    Object borderTag = targetView.getTag(R.id.btn_add_sticker);
                                    int borderStyle = borderTag instanceof Integer
                                            ? (int) borderTag : 0;
                                    applyBorderStyle(gd, borderStyle);
                                    targetView.setBackground(gd);
                                    targetView.setTag(color);
                                    exportToJson();
                        }));
        }

        // ── Close
        TextView btnClose = cv.findViewById(R.id.btn_sel_close);
        if (btnClose != null)
            btnClose.setOnClickListener(v -> dismissSelectionControls());

        // ── Edit Text
        View btnEdit = cv.findViewById(R.id.btn_sel_edit_text);
        if (btnEdit != null) {
            if (isLocked) {
                btnEdit.setEnabled(false);
                btnEdit.setAlpha(0.4f);
            } else {
                btnEdit.setOnClickListener(v -> {
                    dismissSelectionControls();
                    showEditTextDialog(targetView);
                });
            }
        }

        // ── Text Color
        View btnTextColor = cv.findViewById(R.id.btn_sel_text_color);
        if (btnTextColor != null) {
            btnTextColor.setOnClickListener(v -> showTextColorPopup(targetView));
        }

        // ── Delete Text
        View btnDeleteText = cv.findViewById(R.id.btn_sel_delete_text);
        if (btnDeleteText != null) {
            btnDeleteText.setOnClickListener(v -> {
                new androidx.appcompat.app.AlertDialog.Builder(this)
                        .setTitle("Delete Text?")
                        .setMessage("Aa text delete karvu che?")
                        .setPositiveButton("Ha, Delete", (d, w) -> {
                            dismissSelectionControls();
                            mainLayout.removeView(targetView);
                            exportToJson();
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            });
        }

        // ── Lock / Unlock Text
        View btnLockText  = cv.findViewById(R.id.btn_sel_lock_text);
        TextView tvLockIcon  = cv.findViewById(R.id.tv_lock_icon);
        TextView tvLockLabel = cv.findViewById(R.id.tv_lock_label);

        boolean isCurrentlyLocked = lockedViews.contains(targetView);
        if (tvLockIcon  != null) tvLockIcon.setText(isCurrentlyLocked  ? "🔒" : "🔓");
        if (tvLockLabel != null) tvLockLabel.setText(isCurrentlyLocked ? "Unlock" : "Lock");
        if (btnLockText != null) {
            btnLockText.setBackgroundColor(isCurrentlyLocked
                    ? android.graphics.Color.parseColor("#FFF3E0")
                    : android.graphics.Color.parseColor("#E8EAF6"));
            btnLockText.setOnClickListener(v -> {
                boolean locked = lockedViews.contains(targetView);
                if (locked) {
                    lockedViews.remove(targetView);
                    applyTouchListener(targetView);
                    if (tvLockIcon  != null) tvLockIcon.setText("🔓");
                    if (tvLockLabel != null) tvLockLabel.setText("Lock");
                    btnLockText.setBackgroundColor(android.graphics.Color.parseColor("#E8EAF6"));
                    updateLockIcon(targetView);
                    Toast.makeText(this, "Text unlock thayu", Toast.LENGTH_SHORT).show();
                } else {
                    lockedViews.add(targetView);
                    targetView.setOnTouchListener((tv2, ev) -> {
                        if (ev.getAction() == MotionEvent.ACTION_UP) {
                            showLockPopup(targetView);
                        }
                        return true;
                    });
                    if (tvLockIcon  != null) tvLockIcon.setText("🔒");
                    if (tvLockLabel != null) tvLockLabel.setText("Unlock");
                    btnLockText.setBackgroundColor(android.graphics.Color.parseColor("#FFF3E0"));
                    updateLockIcon(targetView);
                    Toast.makeText(this, "Text lock thayu", Toast.LENGTH_SHORT).show();
                }
                exportToJson();
            });
        }

        showPopupAtSavedPosition(cv);
    }


    private void generateQRCode(String text) {
        try {
            com.google.zxing.MultiFormatWriter writer =
                    new com.google.zxing.MultiFormatWriter();
            com.google.zxing.common.BitMatrix matrix =
                    writer.encode(text,
                            com.google.zxing.BarcodeFormat.QR_CODE,
                            400, 400);

            int w = matrix.getWidth();
            int h = matrix.getHeight();
            int[] pixels = new int[w * h];
            for (int y = 0; y < h; y++) {
                for (int x = 0; x < w; x++) {
                    pixels[y * w + x] = matrix.get(x, y)
                            ? Color.BLACK : Color.WHITE;
                }
            }

            android.graphics.Bitmap qrBitmap =
                    android.graphics.Bitmap.createBitmap(
                            pixels, w, h,
                            android.graphics.Bitmap.Config.ARGB_8888);

            ImageView qrSticker = new ImageView(this);
            qrSticker.setImageBitmap(qrBitmap);
            qrSticker.setTag(R.id.btn_set_background, "QR_CODE");

            RelativeLayout.LayoutParams lp =
                    new RelativeLayout.LayoutParams(300, 300);
            lp.addRule(RelativeLayout.CENTER_IN_PARENT);
            qrSticker.setLayoutParams(lp);

            applyTouchListenerForSticker(qrSticker);
            mainLayout.addView(qrSticker);

            Toast.makeText(this, "✅ QR Code add થઈ ગયો!",
                    Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "QR generate error: " + e.getMessage(),
                    Toast.LENGTH_SHORT).show();
        }
    }


    private void showTextColorPopup(StrokeTextView targetView) {
        int[] colors = {
            getColor(R.color.md_black),
            getColor(R.color.md_white),
            getColor(R.color.md_red),
            getColor(R.color.md_pink),
            getColor(R.color.md_purple),
            getColor(R.color.md_deep_purple),
            getColor(R.color.md_indigo),
            getColor(R.color.md_blue),
            getColor(R.color.md_light_blue),
            getColor(R.color.md_cyan),
            getColor(R.color.md_teal),
            getColor(R.color.md_green),
            getColor(R.color.md_light_green),
            getColor(R.color.md_lime),
            getColor(R.color.md_yellow),
            getColor(R.color.md_amber),
            getColor(R.color.md_orange),
            getColor(R.color.md_deep_orange),
            getColor(R.color.md_brown),
            getColor(R.color.md_grey),
            getColor(R.color.md_blue_grey),
        };

        // ── XML Inflate ──
        View root = getLayoutInflater().inflate(R.layout.popup_text_color, null);

        LinearLayout colorRow = root.findViewById(R.id.color_row);
        TextView btnCancel    = root.findViewById(R.id.btn_color_cancel);
        TextView btnDone      = root.findViewById(R.id.btn_color_done);
        TextView btnClose     = root.findViewById(R.id.btn_color_close);
        TextView btnScreenPick = root.findViewById(R.id.btn_screen_pick);

        // ── Views from XML ──
        com.example.newcardmaker.ColorWheelView colorWheel = root.findViewById(R.id.color_wheel);
        android.widget.EditText etHex = root.findViewById(R.id.et_hex_color);
        View hexPreview = root.findViewById(R.id.view_hex_preview);
        TextView btnHexApply = root.findViewById(R.id.btn_hex_apply);

        // Color wheel initial color
        colorWheel.setColor(targetView.getCurrentTextColor());

        // Hex initial value (without #)
        etHex.setText(String.format("%06X", (0xFFFFFF & targetView.getCurrentTextColor())));
        hexPreview.setBackgroundColor(targetView.getCurrentTextColor());

        final int[] selectedColor = {targetView.getCurrentTextColor()};

        // Helper: update all views when color changes
        Runnable updateAll = () -> {
            int c = selectedColor[0];
            targetView.setTextColor(c);
            hexPreview.setBackgroundColor(c);
            String hex = String.format("%06X", (0xFFFFFF & c));
            if (!etHex.getText().toString().equalsIgnoreCase(hex)) {
                etHex.setText(hex);
                etHex.setSelection(hex.length());
            }
        };

        // ── Color Wheel listener ──
        colorWheel.setOnColorChangedListener(c -> {
            selectedColor[0] = c;
            updateAll.run();
        });

        // ── Quick color buttons ──
        float dp = getResources().getDisplayMetrics().density;
        int btnSize = (int)(36 * dp);
        int gap     = (int)(8 * dp);

        for (int c : colors) {
            final int color = c;
            View colorBtn = new View(this);
            GradientDrawable gd = new GradientDrawable();
            gd.setShape(GradientDrawable.OVAL);
            gd.setColor(color);
            gd.setStroke(2, Color.parseColor("#CCCCCC"));
            colorBtn.setBackground(gd);
            LinearLayout.LayoutParams bp = new LinearLayout.LayoutParams(btnSize, btnSize);
            bp.setMargins(0, 0, gap, 0);
            colorBtn.setLayoutParams(bp);
            colorBtn.setOnClickListener(v2 -> {
                selectedColor[0] = color;
                colorWheel.setColor(color);
                updateAll.run();
            });
            colorRow.addView(colorBtn);
        }

        // ── Hex Apply button ──
        btnHexApply.setOnClickListener(v2 -> {
            try {
                String hex = etHex.getText().toString().trim();
                if (!hex.startsWith("#")) hex = "#" + hex;
                int parsed = Color.parseColor(hex);
                selectedColor[0] = parsed;
                colorWheel.setColor(parsed);
                updateAll.run();
            } catch (Exception e) {
                etHex.setError("Invalid color");
            }
        });

        // ── PopupWindow — display width, 180dp height ──
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        int popupHeight = (int)(180 * getResources().getDisplayMetrics().density);
        final PopupWindow popup = new PopupWindow(root,
                screenWidth,
                popupHeight, true);
        popup.setOutsideTouchable(true);
        popup.setElevation(16f);
        popup.setBackgroundDrawable(new android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT));

        // ── Movable — title bar drag ──
        View titleBar = root.findViewById(R.id.tv_color_title);
        final int[] lastX = {0};
        final int[] lastY = {0};
        titleBar.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    lastX[0] = (int) event.getRawX();
                    lastY[0] = (int) event.getRawY();
                    return true;
                case MotionEvent.ACTION_MOVE:
                    int dx2 = (int) event.getRawX() - lastX[0];
                    int dy2 = (int) event.getRawY() - lastY[0];
                    int[] loc = new int[2];
                    root.getLocationOnScreen(loc);
                    popup.update(loc[0] + dx2, loc[1] + dy2, screenWidth, popupHeight);
                    lastX[0] = (int) event.getRawX();
                    lastY[0] = (int) event.getRawY();
                    return true;
            }
            return false;
        });

        // ── Cancel / Done ──
        final int originalColor = targetView.getCurrentTextColor();

        // ── Screen Color Picker ──
        btnScreenPick.setOnClickListener(v2 -> {
            popup.dismiss(); // dismiss = onDismissListener = controls show થશે
            new com.example.newcardmaker.ScreenColorPickerOverlay(this,
                new com.example.newcardmaker.ScreenColorPickerOverlay.OnColorPickedListener() {
                    @Override
                    public void onColorPreview(int color) {
                        targetView.setTextColor(color);
                    }
                    @Override
                    public void onColorPicked(int color) {
                        targetView.setTextColor(color);
                        exportToJson();
                        showTextColorPopup(targetView);
                    }
                    @Override
                    public void onCancelled() {
                        targetView.setTextColor(originalColor);
                        // controls already show થઈ ગયા છે dismiss listener થી
                    }
                }).show();
        });

        btnClose.setOnClickListener(v2 -> {
            targetView.setTextColor(originalColor);
            popup.dismiss();
        });

        btnCancel.setOnClickListener(v2 -> {
            targetView.setTextColor(originalColor);
            popup.dismiss();
        });

        btnDone.setOnClickListener(v2 -> {
            exportToJson();
            popup.dismiss();
        });

        // ── Text Controls hide ──
        if (selectionControlsPopup != null && selectionControlsPopup.isShowing()) {
            selectionControlsPopup.dismiss();
        }

        View anchor = getWindow().getDecorView().getRootView();
        int screenHeight = getResources().getDisplayMetrics().heightPixels;
        int yOffset = (screenHeight - popupHeight) / 2;
        popup.showAtLocation(anchor, Gravity.TOP | Gravity.START, 0, yOffset);

        // ── Text Controls restore when color popup closes ──
        popup.setOnDismissListener(() -> showSelectionControlsForText(targetView));
    }

    private void showTextBorderDialog(StrokeTextView targetView) {

    }

    /**
     * Gradient panel inline inside text controls popup
     */
    private void showInlineGradientPanel(View cv, StrokeTextView targetText) {
        LinearLayout panelAction   = cv.findViewById(R.id.panel_tab_action);
        LinearLayout panelSpacing  = cv.findViewById(R.id.panel_tab_spacing);
        LinearLayout panelTransform= cv.findViewById(R.id.panel_tab_transform);
        LinearLayout panelEffects  = cv.findViewById(R.id.panel_tab_effects);
        LinearLayout panelLayout   = cv.findViewById(R.id.panel_tab_layout);
        LinearLayout panelArc      = cv.findViewById(R.id.panel_arc_controls);
        HorizontalScrollView tabScroll = cv.findViewById(R.id.main_scroll);

        if (panelAction   != null) panelAction.setVisibility(View.GONE);
        if (panelSpacing  != null) panelSpacing.setVisibility(View.GONE);
        if (panelTransform!= null) panelTransform.setVisibility(View.GONE);
        if (panelEffects  != null) panelEffects.setVisibility(View.GONE);
        if (panelLayout   != null) panelLayout.setVisibility(View.GONE);
        if (panelArc      != null) panelArc.setVisibility(View.GONE);
        if (tabScroll     != null) tabScroll.setVisibility(View.GONE);

        int dp8 = dpToPx(8); int dp4 = dpToPx(4);

        android.widget.ScrollView scrollWrapper = new android.widget.ScrollView(this);
        scrollWrapper.setBackgroundColor(Color.parseColor("#F3F4F6"));
        scrollWrapper.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, dpToPx(160)));

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp8, dp4, dp8, dp4);
        root.setBackgroundColor(Color.parseColor("#F3F4F6"));
        scrollWrapper.addView(root);

        // Back row
        LinearLayout backRow = new LinearLayout(this);
        backRow.setOrientation(LinearLayout.HORIZONTAL);
        backRow.setGravity(Gravity.CENTER_VERTICAL);
        backRow.setBackgroundColor(Color.parseColor("#2A3439"));
        backRow.setPadding(dp8, dp4, dp8, dp4);
        backRow.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        TextView btnBack = new TextView(this);
        btnBack.setText("\u2190 Back");
        btnBack.setTextColor(Color.parseColor("#F3F4F6"));
        btnBack.setTextSize(9);
        btnBack.setTypeface(null, android.graphics.Typeface.BOLD);
        btnBack.setPadding(0, dp4, dp4*3, dp4);
        backRow.addView(btnBack);
        TextView gradTitle = new TextView(this);
        gradTitle.setText("\uD83C\uDF08 Gradient");
        gradTitle.setTextColor(Color.WHITE);
        gradTitle.setTextSize(9);
        gradTitle.setGravity(Gravity.CENTER);
        gradTitle.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
        backRow.addView(gradTitle);
        root.addView(backRow);

        // Preview
        StrokeTextView preview = new StrokeTextView(this);
        preview.setText(targetText.getText().toString().isEmpty() ? "Preview" : targetText.getText());
        preview.setTextSize(20);
        preview.setGravity(Gravity.CENTER);
        preview.setPadding(dp8, dp8, dp8, dp8);
        preview.setStrokeWidth(targetText.getStrokeWidth());
        preview.setStrokeColor(targetText.getStrokeColor());
        LinearLayout.LayoutParams prevLp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dpToPx(50));
        prevLp.setMargins(0, dp4, 0, dp4);
        preview.setLayoutParams(prevLp);
        preview.setBackgroundColor(Color.WHITE);
        root.addView(preview);

        final int[] color1 = {Color.parseColor("#FF6B35")};
        final int[] color2 = {Color.parseColor("#667eea")};
        final String[] direction = {"HORIZONTAL"};
        final Runnable[] updatePreview = {null};

        // Color rows helper
        GradientDrawable cb1Gd = new GradientDrawable(); cb1Gd.setColor(color1[0]); cb1Gd.setCornerRadius(6f); cb1Gd.setStroke(1, Color.parseColor("#D1D5DB"));
        GradientDrawable cb2Gd = new GradientDrawable(); cb2Gd.setColor(color2[0]); cb2Gd.setCornerRadius(6f); cb2Gd.setStroke(1, Color.parseColor("#D1D5DB"));
        View colorBox1 = new View(this); colorBox1.setBackground(cb1Gd);
        View colorBox2 = new View(this); colorBox2.setBackground(cb2Gd);
        LinearLayout.LayoutParams cbLp = new LinearLayout.LayoutParams(dpToPx(28), dpToPx(22)); cbLp.setMargins(0, 0, dp8, 0);
        colorBox1.setLayoutParams(cbLp); colorBox2.setLayoutParams(cbLp);

        LinearLayout.LayoutParams cRowLp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dpToPx(34));
        cRowLp.setMargins(0, dpToPx(2), 0, dpToPx(2));

        for (int c = 0; c < 2; c++) {
            final boolean isC1 = (c == 0);
            LinearLayout cRow = new LinearLayout(this);
            cRow.setOrientation(LinearLayout.HORIZONTAL);
            cRow.setGravity(Gravity.CENTER_VERTICAL);
            cRow.setBackgroundColor(Color.WHITE);
            cRow.setPadding(dp8, dp4, dp8, dp4);
            cRow.setLayoutParams(cRowLp);
            TextView lbl = new TextView(this);
            lbl.setText(isC1 ? "Color 1" : "Color 2");
            lbl.setTextSize(8); lbl.setTextColor(Color.parseColor("#6B7280"));
            lbl.setTypeface(null, android.graphics.Typeface.BOLD);
            lbl.setLayoutParams(new LinearLayout.LayoutParams(dpToPx(44), LinearLayout.LayoutParams.WRAP_CONTENT));
            cRow.addView(lbl);
            cRow.addView(isC1 ? colorBox1 : colorBox2);
            TextView btnPick = new TextView(this);
            btnPick.setText("Pick");
            btnPick.setTextSize(8); btnPick.setTextColor(Color.WHITE);
            btnPick.setGravity(Gravity.CENTER);
            btnPick.setBackgroundColor(Color.parseColor("#607D8B"));
            btnPick.setPadding(dp8, dp4, dp8, dp4);
            btnPick.setOnClickListener(vv -> showColorPickerPopup(
                        isC1 ? color1[0] : color2[0],
                        color -> {
                            if (isC1) { color1[0] = color; cb1Gd.setColor(color); }
                    else       { color2[0] = color; cb2Gd.setColor(color); }
                    if (updatePreview[0] != null) updatePreview[0].run();
                        }));
            cRow.addView(btnPick);
            root.addView(cRow);
        }

        // Direction row
        LinearLayout dirRow = new LinearLayout(this);
        dirRow.setOrientation(LinearLayout.HORIZONTAL);
        dirRow.setGravity(Gravity.CENTER_VERTICAL);
        dirRow.setBackgroundColor(Color.WHITE);
        dirRow.setPadding(dp8, dp4, dp8, dp4);
        LinearLayout.LayoutParams drLp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dpToPx(32));
        drLp.setMargins(0, dpToPx(2), 0, dpToPx(2));
        dirRow.setLayoutParams(drLp);
        TextView lblDir = new TextView(this); lblDir.setText("Dir"); lblDir.setTextSize(8); lblDir.setTextColor(Color.parseColor("#6B7280")); lblDir.setTypeface(null, android.graphics.Typeface.BOLD);
        lblDir.setLayoutParams(new LinearLayout.LayoutParams(dpToPx(44), LinearLayout.LayoutParams.WRAP_CONTENT));
        dirRow.addView(lblDir);
        TextView btnH = new TextView(this); btnH.setText("\u2194 H"); btnH.setTextSize(8); btnH.setTextColor(Color.WHITE); btnH.setGravity(Gravity.CENTER); btnH.setBackgroundColor(Color.parseColor("#2A3439")); btnH.setPadding(dp8, dp4, dp8, dp4);
        LinearLayout.LayoutParams hLp = new LinearLayout.LayoutParams(0, dpToPx(22), 1f); hLp.setMargins(0, 0, dp4, 0); btnH.setLayoutParams(hLp);
        dirRow.addView(btnH);
        TextView btnV = new TextView(this); btnV.setText("\u2195 V"); btnV.setTextSize(8); btnV.setTextColor(Color.parseColor("#374151")); btnV.setGravity(Gravity.CENTER); btnV.setBackgroundColor(Color.parseColor("#D1D5DB")); btnV.setPadding(dp8, dp4, dp8, dp4);
        btnV.setLayoutParams(new LinearLayout.LayoutParams(0, dpToPx(22), 1f));
        dirRow.addView(btnV);
        root.addView(dirRow);

        // Preset row
        LinearLayout presetRow = new LinearLayout(this);
        presetRow.setOrientation(LinearLayout.HORIZONTAL);
        presetRow.setPadding(dp8, dp4, dp8, dp4);
        presetRow.setBackgroundColor(Color.WHITE);
        LinearLayout.LayoutParams prLp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dpToPx(36));
        prLp.setMargins(0, dpToPx(2), 0, dpToPx(2));
        presetRow.setLayoutParams(prLp);
        int[][] presets = {{Color.parseColor("#FF6B35"), Color.parseColor("#F7C59F")},{Color.parseColor("#667eea"), Color.parseColor("#764ba2")},{Color.parseColor("#11998e"), Color.parseColor("#38ef7d")},{Color.parseColor("#F7971E"), Color.parseColor("#FFD200")},{Color.parseColor("#FF416C"), Color.parseColor("#FF4B2B")},{Color.parseColor("#4facfe"), Color.parseColor("#00f2fe")}};
        for (int[] p : presets) {
            GradientDrawable swBg = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, new int[]{p[0], p[1]}); swBg.setCornerRadius(6f);
            View swatch = new View(this); swatch.setBackground(swBg);
            LinearLayout.LayoutParams swLp2 = new LinearLayout.LayoutParams(0, dpToPx(24), 1f); swLp2.setMargins(dpToPx(2), 0, dpToPx(2), 0); swatch.setLayoutParams(swLp2);
            final int pc1 = p[0], pc2 = p[1];
            swatch.setOnClickListener(vv -> { color1[0] = pc1; color2[0] = pc2; cb1Gd.setColor(pc1); cb2Gd.setColor(pc2); if (updatePreview[0] != null) updatePreview[0].run(); });
            presetRow.addView(swatch);
        }
        root.addView(presetRow);

        // Apply / Remove row
        LinearLayout actRow = new LinearLayout(this);
        actRow.setOrientation(LinearLayout.HORIZONTAL);
        actRow.setPadding(dp8, dp4, dp8, dp4);
        LinearLayout.LayoutParams arLp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dpToPx(34));
        arLp.setMargins(0, dpToPx(2), 0, 0);
        actRow.setLayoutParams(arLp);
        TextView btnApply = new TextView(this); btnApply.setText("\u2705 Apply"); btnApply.setTextSize(9); btnApply.setTextColor(Color.WHITE); btnApply.setGravity(Gravity.CENTER); btnApply.setBackgroundColor(Color.parseColor("#2A3439"));
        LinearLayout.LayoutParams apLp = new LinearLayout.LayoutParams(0, dpToPx(26), 1f); apLp.setMargins(0, 0, dp4, 0); btnApply.setLayoutParams(apLp);
        actRow.addView(btnApply);
        TextView btnRemove = new TextView(this); btnRemove.setText("\u2715 Remove"); btnRemove.setTextSize(9); btnRemove.setTextColor(Color.parseColor("#374151")); btnRemove.setGravity(Gravity.CENTER); btnRemove.setBackgroundColor(Color.parseColor("#D1D5DB"));
        btnRemove.setLayoutParams(new LinearLayout.LayoutParams(0, dpToPx(26), 1f));
        actRow.addView(btnRemove);
        root.addView(actRow);

        // Add to popup
        LinearLayout scrollParent = cv.findViewById(R.id.panel_main_content);
        if (scrollParent != null) scrollParent.addView(scrollWrapper);

        updatePreview[0] = () -> applyGradientToView(preview, color1[0], color2[0], direction[0]);
        updatePreview[0].run();

        btnH.setOnClickListener(vv -> { direction[0] = "HORIZONTAL"; btnH.setBackgroundColor(Color.parseColor("#2A3439")); btnH.setTextColor(Color.WHITE); btnV.setBackgroundColor(Color.parseColor("#D1D5DB")); btnV.setTextColor(Color.parseColor("#374151")); updatePreview[0].run(); });
        btnV.setOnClickListener(vv -> { direction[0] = "VERTICAL"; btnV.setBackgroundColor(Color.parseColor("#2A3439")); btnV.setTextColor(Color.WHITE); btnH.setBackgroundColor(Color.parseColor("#D1D5DB")); btnH.setTextColor(Color.parseColor("#374151")); updatePreview[0].run(); });

        Runnable backAction = () -> {
            if (scrollParent != null) scrollParent.removeView(scrollWrapper);
            if (panelAction != null) panelAction.setVisibility(View.VISIBLE);
            if (tabScroll   != null) tabScroll.setVisibility(View.VISIBLE);
        };
        btnBack.setOnClickListener(vv -> backAction.run());
        btnApply.setOnClickListener(vv -> {
            applyGradientToView(targetText, color1[0], color2[0], direction[0]);
            targetText.setTag(R.id.tv_move_speed, color1[0] + "," + color2[0] + "," + direction[0]);
            targetText.invalidate(); exportToJson();
            backAction.run();
        });
        btnRemove.setOnClickListener(vv -> {
            targetText.getPaint().setShader(null);
            targetText.setTag(R.id.tv_move_speed, null);
            targetText.invalidate(); exportToJson();
            backAction.run();
        });
    }

    /**
     * Gradient — movable PopupWindow
     */
    private void showGradientPopupWindow(final StrokeTextView targetText) {
        int dp4 = dpToPx(4), dp6 = dpToPx(6), dp8 = dpToPx(8);

        // 100+ gradient presets {color1, color2, name}
        final Object[][] GRADIENTS = {
            // Warm
            {0xFFFF6B35, 0xFFF7C59F, "Sunset"},
            {0xFFFF416C, 0xFFFF4B2B, "Fire"},
            {0xFFF7971E, 0xFFFFD200, "Gold"},
            {0xFFFF9A9E, 0xFFFAD0C4, "Rose"},
            {0xFFFF9F43, 0xFFFF6B6B, "Peach"},
            {0xFFFFB347, 0xFFFFCC02, "Amber"},
            {0xFFFF6B6B, 0xFFFFE66D, "Warm Sun"},
            {0xFFFF4E50, 0xFFF9D423, "Flame"},
            {0xFFFF8C00, 0xFFFF0080, "Neon Warm"},
            {0xFFFC4A1A, 0xFFF7B733, "Fire Gold"},
            {0xFFED213A, 0xFF93291E, "Deep Red"},
            {0xFFFF8008, 0xFFFFC837, "Orange Sun"},
            {0xFFDA4453, 0xFF89216B, "Crimson"},
            {0xFFBD3F32, 0xFFCB8B2A, "Rust"},
            {0xFFFF5F6D, 0xFFFFC371, "Coral"},
            // Cool
            {0xFF667EEA, 0xFF764BA2, "Purple"},
            {0xFF4FACFE, 0xFF00F2FE, "Sky"},
            {0xFF43E97B, 0xFF38F9D7, "Mint"},
            {0xFF11998E, 0xFF38EF7D, "Emerald"},
            {0xFF4776E6, 0xFF8E54E9, "Blue Purple"},
            {0xFF0099F7, 0xFFF11712, "Triton"},
            {0xFF02AABD, 0xFF00CDAC, "Teal"},
            {0xFF56CCF2, 0xFF2F80ED, "Ocean"},
            {0xFF6A3093, 0xFFA044FF, "Violet"},
            {0xFF1A1A2E, 0xFF16213E, "Midnight"},
            {0xFF000428, 0xFF004E92, "Deep Sea"},
            {0xFF0F2027, 0xFF203A43, "Slate"},
            {0xFF2193B0, 0xFF6DD5FA, "Aqua"},
            {0xFF373B44, 0xFF4286F4, "Storm"},
            {0xFF5C258D, 0xFF4389A2, "Indigo"},
            // Nature
            {0xFF56AB2F, 0xFFA8E063, "Green"},
            {0xFF134E5E, 0xFF71B280, "Forest"},
            {0xFF1D976C, 0xFF93F9B9, "Jungle"},
            {0xFF52C234, 0xFF061700, "Nature"},
            {0xFF005C97, 0xFF363795, "Deep Blue"},
            {0xFF1488CC, 0xFF2B32B2, "Royal Blue"},
            {0xFF2980B9, 0xFF2C3E50, "Marine"},
            {0xFF3A7BD5, 0xFF3A6073, "Calm"},
            {0xFF16A085, 0xFFF4D03F, "Turquoise Gold"},
            {0xFF27AE60, 0xFF1ABC9C, "Fresh"},
            // Pastel
            {0xFFA18CD1, 0xFFFBC2EB, "Lavender"},
            {0xFFFDA085, 0xFFF6D365, "Peach Cream"},
            {0xFFD4FC79, 0xFF96E6A1, "Lime"},
            {0xFFFCB69F, 0xFFFF9A9E, "Blush"},
            {0xFFE0C3FC, 0xFF8EC5FC, "Soft Purple"},
            {0xFFFFF1EB, 0xFFACE0F9, "Cotton Candy"},
            {0xFFD9AFD9, 0xFF97D9E1, "Lilac"},
            {0xFFCFDEF3, 0xFFE0EAFC, "Baby Blue"},
            {0xFFFDDB92, 0xFF1FA2FF, "Retro"},
            {0xFFFCCCCC, 0xFFFFCCCC, "Baby Pink"},
            // Vibrant
            {0xFF00B4D8, 0xFF90E0EF, "Cyan"},
            {0xFFE96C6C, 0xFFF9C784, "Salsa"},
            {0xFF8EC5FC, 0xFFE0C3FC, "Dream"},
            {0xFF00C6FF, 0xFF0072FF, "Electric Blue"},
            {0xFF7F00FF, 0xFFE100FF, "Neon Purple"},
            {0xFF00F260, 0xFF0575E6, "Matrix"},
            {0xFFFC00FF, 0xFF00DBDE, "Neon Cyan"},
            {0xFFFFFF00, 0xFFFF00FF, "Neon Yellow"},
            {0xFFFF0099, 0xFF493240, "Hot Pink"},
            {0xFF00D2FF, 0xFF3A7BD5, "Sky Blue"},
            {0xFF6EE2F5, 0xFF6454F0, "Electric"},
            {0xFFF953C6, 0xFFB91D73, "Magenta"},
            {0xFFB24592, 0xFFF15F79, "Pink Purple"},
            {0xFFDE6161, 0xFF2657EB, "Mixed"},
            {0xFF66FF00, 0xFF00FFCC, "Neon Green"},
            // Dark
            {0xFF2C3E50, 0xFF4CA1AF, "Dark Teal"},
            {0xFF232526, 0xFF414345, "Charcoal"},
            {0xFF0F0C29, 0xFF302B63, "Dark Purple"},
            {0xFF16213E, 0xFF0F3460, "Navy"},
            {0xFF1A1A2E, 0xFF533483, "Galactic"},
            {0xFF2D3561, 0xFFC05C7E, "Twilight"},
            {0xFF4B1248, 0xFFF10711, "Dark Red"},
            {0xFF403B4A, 0xFFE7E9BB, "Dark Warm"},
            {0xFF000000, 0xFF434343, "Carbon"},
            {0xFF11998E, 0xFF2980B9, "Dark Green Blue"},
            // Sunset/Sunrise
            {0xFFf83600, 0xFFf9d423, "Sunrise"},
            {0xFFf7971e, 0xFFffd200, "Golden Hour"},
            {0xFFff6e7f, 0xFFbfe9ff, "Dusk"},
            {0xFFfc5c7d, 0xFF6a3093, "Twilight Rose"},
            {0xFFee0979, 0xFFff6a00, "Sunset Fire"},
            {0xFFf64f59, 0xFFc471ed, "Pink Fire"},
            {0xFFff9966, 0xFFff5e62, "Warm Dusk"},
            {0xFFfddb92, 0xFFd1fdff, "Morning"},
            // Special
            {0xFFFFFFFF, 0xFF000000, "B&W"},
            {0xFF000000, 0xFFFFFFFF, "Dark Light"},
            {0xFFc0c0c0, 0xFFFFFFFF, "Silver"},
            {0xFFFFD700, 0xFFFFA500, "Gold Shine"},
            {0xFFe8d5b7, 0xFFe8d5b7, "Cream"},
            {0xFF667eea, 0xFF764ba2, "Royal"},
            {0xFF355C7D, 0xFF6C5B7B, "Denim"},
            {0xFF40E0D0, 0xFFFF8C00, "Tropical"},
            {0xFFbdc3c7, 0xFF2c3e50, "Steel"},
            {0xFFc94b4b, 0xFF4b134f, "Wine"},
            {0xFF11c5cf, 0xFFaf2ee5, "Aurora"},
            {0xFF0081C6, 0xFF00C6FF, "Water"},
            {0xFFa8e6cf, 0xFFdcedc1, "Sage"},
            {0xFFff9a9e, 0xFFfecfef, "Sakura"},
            {0xFF764ba2, 0xFFf093fb, "Galaxy"},
            {0xFF6a3093, 0xFFa044ff, "Deep Violet"},
            {0xFF43b89c, 0xFF28a0b5, "Seafoam"},
            {0xFFff6a00, 0xFFee0979, "Hot Sunset"},
            {0xFF00c3ff, 0xFFffff1c, "Neon Day"},
            {0xFF833ab4, 0xFFfd1d1d, "Instagram"},
        };

        // ── Root layout
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(Color.parseColor("#F3F4F6"));

        // ── Drag handle
        LinearLayout dragRow = new LinearLayout(this);
        dragRow.setOrientation(LinearLayout.HORIZONTAL);
        dragRow.setGravity(Gravity.CENTER_VERTICAL);
        dragRow.setBackgroundColor(Color.parseColor("#2A3439"));
        dragRow.setPadding(dp8, dp4, dp6, dp4);
        dragRow.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, dpToPx(30)));
        TextView dragHandle = new TextView(this);
        dragHandle.setText("\u2261  Gradient Color");
        dragHandle.setTextColor(Color.parseColor("#F3F4F6"));
        dragHandle.setTextSize(9);
        dragHandle.setTypeface(null, android.graphics.Typeface.BOLD);
        dragHandle.setLayoutParams(new LinearLayout.LayoutParams(0,
                LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
        dragRow.addView(dragHandle);
        TextView btnCloseG = new TextView(this);
        btnCloseG.setText("\u2715");
        btnCloseG.setTextColor(Color.WHITE);
        btnCloseG.setTextSize(11);
        btnCloseG.setGravity(Gravity.CENTER);
        btnCloseG.setBackgroundColor(Color.parseColor("#607D8B"));
        btnCloseG.setPadding(dp8, dp4, dp8, dp4);
        btnCloseG.setTypeface(null, android.graphics.Typeface.BOLD);
        dragRow.addView(btnCloseG);
        root.addView(dragRow);

        // ── Remove button row
        LinearLayout removeRow = new LinearLayout(this);
        removeRow.setOrientation(LinearLayout.HORIZONTAL);
        removeRow.setBackgroundColor(Color.WHITE);
        removeRow.setPadding(dp8, dp4, dp8, dp4);
        removeRow.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, dpToPx(32)));
        TextView btnRemoveG = new TextView(this);
        btnRemoveG.setText("\u2715  Remove Gradient");
        btnRemoveG.setTextSize(9);
        btnRemoveG.setTextColor(Color.WHITE);
        btnRemoveG.setGravity(Gravity.CENTER);
        btnRemoveG.setBackgroundColor(Color.parseColor("#374151"));
        btnRemoveG.setPadding(dp8, dp4, dp8, dp4);
        btnRemoveG.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, dpToPx(24)));
        removeRow.addView(btnRemoveG);
        root.addView(removeRow);

        // Divider
        View div = new View(this);
        div.setBackgroundColor(Color.parseColor("#D1D5DB"));
        div.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 1));
        root.addView(div);

        // ── Grid scroll
        android.widget.ScrollView sv = new android.widget.ScrollView(this);
        android.widget.GridLayout grid = new android.widget.GridLayout(this);
        grid.setColumnCount(3);
        grid.setPadding(dp4, dp4, dp4, dp4);
        grid.setBackgroundColor(Color.parseColor("#F3F4F6"));

        android.util.DisplayMetrics dm = new android.util.DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int popW = (int)(dm.widthPixels * 0.88f);
        int cellW = (popW - dp4 * 2) / 3;

        for (Object[] g : GRADIENTS) {
            int c1 = (int)(Integer) g[0];
            int c2 = (int)(Integer) g[1];
            String name = (String) g[2];

            LinearLayout cell = new LinearLayout(this);
            cell.setOrientation(LinearLayout.VERTICAL);
            cell.setGravity(Gravity.CENTER);
            android.widget.GridLayout.LayoutParams cellLp =
                    new android.widget.GridLayout.LayoutParams();
            cellLp.width = cellW;
            cellLp.height = dpToPx(52);
            cellLp.setMargins(dpToPx(2), dpToPx(2), dpToPx(2), dpToPx(2));
            cell.setLayoutParams(cellLp);

            // Gradient swatch
            View swatch = new View(this);
            GradientDrawable gd = new GradientDrawable(
                    GradientDrawable.Orientation.LEFT_RIGHT, new int[]{c1, c2});
            gd.setCornerRadius(dpToPx(6));
            swatch.setBackground(gd);
            swatch.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, dpToPx(32)));
            cell.addView(swatch);

            // Name label
            TextView nameTv = new TextView(this);
            nameTv.setText(name);
            nameTv.setTextSize(6.5f);
            nameTv.setTextColor(Color.parseColor("#374151"));
            nameTv.setGravity(Gravity.CENTER);
            nameTv.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            cell.addView(nameTv);

            // Click → real-time apply
            cell.setOnClickListener(vv -> {
                applyGradientToView(targetText, c1, c2, "HORIZONTAL");
                targetText.setTag(R.id.tv_move_speed, c1 + "," + c2 + ",HORIZONTAL");
                targetText.setStrokeWidth(targetText.getStrokeWidth());
                targetText.setStrokeColor(targetText.getStrokeColor());
                targetText.invalidate();
                exportToJson();
            });

            grid.addView(cell);
        }

        sv.addView(grid);
        root.addView(sv);

        // ── PopupWindow
        PopupWindow gradPopup = new PopupWindow(root, popW,
                (int)(dm.heightPixels * 0.55f), true);
        gradPopup.setBackgroundDrawable(
                new android.graphics.drawable.ColorDrawable(Color.TRANSPARENT));
        gradPopup.setElevation(20f);
        gradPopup.setOutsideTouchable(true);
        int initX = (dm.widthPixels - popW) / 2;
        int initY = dm.heightPixels / 6;
        gradPopup.showAtLocation(getWindow().getDecorView().getRootView(),
                Gravity.TOP | Gravity.LEFT, initX, initY);

        // ── Drag
        final float[] lastDrag = {0, 0};
        final int[] popXY = {initX, initY};
        dragHandle.setOnTouchListener((vv, ev) -> {
            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    lastDrag[0] = ev.getRawX(); lastDrag[1] = ev.getRawY();
                    int[] loc = new int[2]; root.getLocationOnScreen(loc);
                    int[] mLoc = new int[2]; mainLayout.getLocationOnScreen(mLoc);
                    popXY[0] = loc[0] - mLoc[0]; popXY[1] = loc[1] - mLoc[1];
                    break;
                case MotionEvent.ACTION_MOVE:
                    popXY[0] += (int)(ev.getRawX() - lastDrag[0]);
                    popXY[1] += (int)(ev.getRawY() - lastDrag[1]);
                    lastDrag[0] = ev.getRawX(); lastDrag[1] = ev.getRawY();
                    gradPopup.update(popXY[0], popXY[1], -1, -1);
                    break;
            }
            return true;
        });

        btnCloseG.setOnClickListener(vv -> gradPopup.dismiss());
        btnRemoveG.setOnClickListener(vv -> {
            targetText.getPaint().setShader(null);
            targetText.setTag(R.id.tv_move_speed, null);
            targetText.invalidate();
            exportToJson();
            gradPopup.dismiss();
        });
    }

    private void showGradientColorDialog(final StrokeTextView targetText) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("🌈 Gradient Text Color");

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(24, 20, 24, 16);

        // ── Live Preview
        StrokeTextView preview = new StrokeTextView(this);
        preview.setText(targetText.getText().toString().isEmpty() ? "Preview Text" : targetText.getText());
        preview.setTextSize(24);
        preview.setGravity(Gravity.CENTER);
        preview.setPadding(20, 30, 20, 30);
        preview.setStrokeWidth(targetText.getStrokeWidth());
        preview.setStrokeColor(targetText.getStrokeColor());
        LinearLayout.LayoutParams prevLp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 120);
        prevLp.setMargins(0, 0, 0, 16);
        preview.setLayoutParams(prevLp);
        preview.setBackgroundColor(Color.parseColor("#F5F5F5"));
        root.addView(preview);

        // ── State
        final int[] color1 = {Color.parseColor("#FF6B35")};
        final int[] color2 = {Color.parseColor("#667eea")};
        final String[] direction = {"HORIZONTAL"};

        // ── Color 1 row
        TextView lbl1 = new TextView(this);
        lbl1.setText("Color 1:");
        lbl1.setTextSize(13);
        lbl1.setTextColor(Color.DKGRAY);
        root.addView(lbl1);

        LinearLayout c1Row = new LinearLayout(this);
        c1Row.setOrientation(LinearLayout.HORIZONTAL);
        c1Row.setGravity(Gravity.CENTER_VERTICAL);
        LinearLayout.LayoutParams cRowLp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        cRowLp.setMargins(0, 4, 0, 8);
        c1Row.setLayoutParams(cRowLp);

        View colorBox1 = new View(this);
        colorBox1.setBackgroundColor(color1[0]);
        GradientDrawable cb1Gd = new GradientDrawable();
        cb1Gd.setColor(color1[0]);
        cb1Gd.setCornerRadius(8f);
        cb1Gd.setStroke(2, Color.parseColor("#CCCCCC"));
        colorBox1.setBackground(cb1Gd);
        LinearLayout.LayoutParams cbLp = new LinearLayout.LayoutParams(60, 44);
        cbLp.setMargins(0, 0, 12, 0);
        colorBox1.setLayoutParams(cbLp);

        Button btnColor1 = new Button(this);
        btnColor1.setText("🎨 Pick Color 1");
        btnColor1.setTextSize(12);
        btnColor1.setTextColor(Color.WHITE);
        btnColor1.setBackgroundColor(Color.parseColor("#1565C0"));
        btnColor1.setLayoutParams(new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

        c1Row.addView(colorBox1);
        c1Row.addView(btnColor1);
        root.addView(c1Row);

        // ── Color 2 row
        TextView lbl2 = new TextView(this);
        lbl2.setText("Color 2:");
        lbl2.setTextSize(13);
        lbl2.setTextColor(Color.DKGRAY);
        root.addView(lbl2);

        LinearLayout c2Row = new LinearLayout(this);
        c2Row.setOrientation(LinearLayout.HORIZONTAL);
        c2Row.setGravity(Gravity.CENTER_VERTICAL);
        c2Row.setLayoutParams(cRowLp);

        View colorBox2 = new View(this);
        GradientDrawable cb2Gd = new GradientDrawable();
        cb2Gd.setColor(color2[0]);
        cb2Gd.setCornerRadius(8f);
        cb2Gd.setStroke(2, Color.parseColor("#CCCCCC"));
        colorBox2.setBackground(cb2Gd);
        colorBox2.setLayoutParams(cbLp);

        Button btnColor2 = new Button(this);
        btnColor2.setText("🎨 Pick Color 2");
        btnColor2.setTextSize(12);
        btnColor2.setTextColor(Color.WHITE);
        btnColor2.setBackgroundColor(Color.parseColor("#1565C0"));
        btnColor2.setLayoutParams(new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

        c2Row.addView(colorBox2);
        c2Row.addView(btnColor2);
        root.addView(c2Row);

        // ── Direction buttons
        TextView lblDir = new TextView(this);
        lblDir.setText("Direction:");
        lblDir.setTextSize(13);
        lblDir.setTextColor(Color.DKGRAY);
        lblDir.setPadding(0, 8, 0, 4);
        root.addView(lblDir);

        LinearLayout dirRow = new LinearLayout(this);
        dirRow.setOrientation(LinearLayout.HORIZONTAL);

        Button btnH = new Button(this);
        btnH.setText("↔ Horizontal");
        btnH.setTextSize(12);
        btnH.setTextColor(Color.WHITE);
        btnH.setBackgroundColor(Color.parseColor("#1565C0")); // active by default
        LinearLayout.LayoutParams dirLp = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        dirLp.setMargins(0, 0, 6, 0);
        btnH.setLayoutParams(dirLp);

        Button btnV = new Button(this);
        btnV.setText("↕ Vertical");
        btnV.setTextSize(12);
        btnV.setTextColor(Color.WHITE);
        btnV.setBackgroundColor(Color.parseColor("#90CAF9"));
        btnV.setLayoutParams(new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

        dirRow.addView(btnH);
        dirRow.addView(btnV);
        root.addView(dirRow);

        // ── Preset gradients
        TextView lblPreset = new TextView(this);
        lblPreset.setText("Quick Presets:");
        lblPreset.setTextSize(13);
        lblPreset.setTextColor(Color.DKGRAY);
        lblPreset.setPadding(0, 14, 0, 6);
        root.addView(lblPreset);

        int[][] presets = {
                {Color.parseColor("#FF6B35"), Color.parseColor("#F7C59F")},
                {Color.parseColor("#667eea"), Color.parseColor("#764ba2")},
                {Color.parseColor("#11998e"), Color.parseColor("#38ef7d")},
                {Color.parseColor("#F7971E"), Color.parseColor("#FFD200")},
                {Color.parseColor("#FF416C"), Color.parseColor("#FF4B2B")},
                {Color.parseColor("#4facfe"), Color.parseColor("#00f2fe")},
        };
        String[] presetNames = {"Sunset", "Purple", "Green", "Gold", "Red", "Sky"};

        LinearLayout presetRow = new LinearLayout(this);
        presetRow.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams prLp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        prLp.setMargins(0, 0, 0, 8);
        presetRow.setLayoutParams(prLp);

        Runnable[] updatePreview = {null}; // forward reference

        for (int i = 0; i < presets.length; i++) {
            final int[] p = presets[i];

            // Gradient preview swatch
            LinearLayout swatch = new LinearLayout(this);
            swatch.setOrientation(LinearLayout.HORIZONTAL);

            View left = new View(this);
            left.setBackgroundColor(p[0]);
            left.setLayoutParams(new LinearLayout.LayoutParams(0, 44, 1f));

            View right = new View(this);
            right.setBackgroundColor(p[1]);
            right.setLayoutParams(new LinearLayout.LayoutParams(0, 44, 1f));

            swatch.addView(left);
            swatch.addView(right);

            GradientDrawable swatchBg = new GradientDrawable(
                    GradientDrawable.Orientation.LEFT_RIGHT, new int[]{p[0], p[1]});
            swatchBg.setCornerRadius(10f);
            swatch.setBackground(swatchBg);

            LinearLayout.LayoutParams swLp = new LinearLayout.LayoutParams(0, 44, 1f);
            swLp.setMargins(3, 0, 3, 0);
            swatch.setLayoutParams(swLp);

            swatch.setOnClickListener(v -> {
                color1[0] = p[0];
                color2[0] = p[1];
                ((GradientDrawable) colorBox1.getBackground()).setColor(p[0]);
                ((GradientDrawable) colorBox2.getBackground()).setColor(p[1]);
                if (updatePreview[0] != null) updatePreview[0].run();
            });

            presetRow.addView(swatch);
        }
        root.addView(presetRow);

        // ── updatePreview define
        updatePreview[0] = () -> applyGradientToView(preview, color1[0], color2[0], direction[0]);

        // ── Initial apply
        updatePreview[0].run();

        // ── Color picker clicks
        btnColor1.setOnClickListener(v ->
                showColorPickerPopup(
                        color1[0],
                        color -> {
                            color1[0] = color;
                                ((GradientDrawable) colorBox1.getBackground()).setColor(color);
                                updatePreview[0].run();
                        }));

        btnColor2.setOnClickListener(v ->
                showColorPickerPopup(
                        color2[0],
                        color -> {
                            color2[0] = color;
                                ((GradientDrawable) colorBox2.getBackground()).setColor(color);
                                updatePreview[0].run();
                        }));

        btnH.setOnClickListener(v -> {
            direction[0] = "HORIZONTAL";
            btnH.setBackgroundColor(Color.parseColor("#1565C0"));
            btnV.setBackgroundColor(Color.parseColor("#90CAF9"));
            updatePreview[0].run();
        });

        btnV.setOnClickListener(v -> {
            direction[0] = "VERTICAL";
            btnV.setBackgroundColor(Color.parseColor("#1565C0"));
            btnH.setBackgroundColor(Color.parseColor("#90CAF9"));
            updatePreview[0].run();
        });

        builder.setView(new ScrollView(this) {{
            addView(root);
        }});

        builder.setPositiveButton("✅ Apply", (d, w) -> {
            applyGradientToView(targetText, color1[0], color2[0], direction[0]);

            // gradient save tag
            targetText.setTag(R.id.tv_move_speed,
                    color1[0] + "," + color2[0] + "," + direction[0]);

            // stroke ફરી apply રાખો
            targetText.setStrokeWidth(targetText.getStrokeWidth());
            targetText.setStrokeColor(targetText.getStrokeColor());

            targetText.invalidate();
            targetText.requestLayout();

            saveCurrentPage();
            exportToJson();

            Toast.makeText(this, "✅ Gradient apply!", Toast.LENGTH_SHORT).show();
        });


        builder.setNeutralButton("✕ Remove", (d, w) -> {
            targetText.getPaint().setShader(null);
            targetText.setTag(R.id.tv_move_speed, null);
            targetText.invalidate();
            exportToJson();
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    // ── Gradient apply helper
    private void applyGradientToView(final StrokeTextView tv,
                                     final int c1, final int c2,
                                     final String dir) {
        tv.post(() -> {
            if (tv.getWidth() == 0) {
                tv.postDelayed(() -> applyGradientToView(tv, c1, c2, dir), 100);
                return;
            }

            android.graphics.LinearGradient shader;

            if ("VERTICAL".equals(dir)) {
                shader = new android.graphics.LinearGradient(
                        0, 0, 0, tv.getHeight(),
                        new int[]{c1, c2}, null,
                        android.graphics.Shader.TileMode.CLAMP);
            } else {
                shader = new android.graphics.LinearGradient(
                        0, 0, tv.getWidth(), 0,
                        new int[]{c1, c2}, null,
                        android.graphics.Shader.TileMode.CLAMP);
            }

            // ✅ getPaint().setShader() ની જગ્યાએ setTextGradient() use
            // આ StrokeTextView ના textShader field માં store થશે
            tv.setTextGradient(shader);
            tv.invalidate();
        });
    }


    private void showTextBgImageDialog(final StrokeTextView targetView) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("🌄 Text Background Image");

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(24, 20, 24, 16);
        root.setBackgroundColor(Color.parseColor("#F5F5F5"));

        // ── Current preview
        TextView lblCurrent = new TextView(this);
        lblCurrent.setText("Current Background:");
        lblCurrent.setTextSize(13);
        lblCurrent.setTextColor(Color.parseColor("#333333"));
        lblCurrent.setPadding(0, 0, 0, 8);
        root.addView(lblCurrent);

        // ── Preview box — show current bg
        final ImageView previewBox = new ImageView(this);
        previewBox.setScaleType(ImageView.ScaleType.CENTER_CROP);
        previewBox.setBackgroundColor(Color.parseColor("#E0E0E0"));
        LinearLayout.LayoutParams prevP = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 120);
        prevP.setMargins(0, 0, 0, 16);
        previewBox.setLayoutParams(prevP);

        // ── Show existing BG image if set
        Object existingBg = targetView.getTag(R.id.btn_sticker_gallery);
        if (existingBg != null && !existingBg.toString().isEmpty()) {
            String bgUri = existingBg.toString();
            if (bgUri.startsWith("http")) {
                Glide.with(this).load(bgUri).into(previewBox);
            } else if (bgUri.startsWith("file://")) {
                Glide.with(this).load(new java.io.File(bgUri.replace("file://", ""))).into(previewBox);
            }
        } else {
            previewBox.setImageResource(android.R.drawable.ic_menu_gallery);
        }
        root.addView(previewBox);

        // ── Option buttons row
        LinearLayout btnRow = new LinearLayout(this);
        btnRow.setOrientation(LinearLayout.HORIZONTAL);
        btnRow.setGravity(android.view.Gravity.CENTER);
        LinearLayout.LayoutParams brP = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        brP.setMargins(0, 0, 0, 12);
        btnRow.setLayoutParams(brP);

        // ── Gallery button
        Button btnGallery = new Button(this);
        btnGallery.setText("📷 Gallery");
        btnGallery.setTextColor(Color.WHITE);
        btnGallery.setBackgroundColor(Color.parseColor("#1565C0"));
        LinearLayout.LayoutParams bgP = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        bgP.setMargins(0, 0, 8, 0);
        btnGallery.setLayoutParams(bgP);

        // ── Remove BG button
        Button btnRemoveBg = new Button(this);
        btnRemoveBg.setText("✕ Remove");
        btnRemoveBg.setTextColor(Color.WHITE);
        btnRemoveBg.setBackgroundColor(Color.parseColor("#C62828"));
        btnRemoveBg.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

        btnRow.addView(btnGallery);
        btnRow.addView(btnRemoveBg);
        root.addView(btnRow);

        // ── Opacity label
        final TextView lblOpacity = new TextView(this);
        lblOpacity.setText("Opacity: 100%");
        lblOpacity.setTextSize(13);
        lblOpacity.setTextColor(Color.parseColor("#333333"));
        root.addView(lblOpacity);

        // ── Opacity seekbar
        final android.widget.SeekBar seekOpacity = new android.widget.SeekBar(this);
        seekOpacity.setMax(100);
        seekOpacity.setProgress(100);
        LinearLayout.LayoutParams soP = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        soP.setMargins(0, 4, 0, 12);
        seekOpacity.setLayoutParams(soP);
        root.addView(seekOpacity);

        // ── Scale mode label
        TextView lblScale = new TextView(this);
        lblScale.setText("Scale Mode:");
        lblScale.setTextSize(13);
        lblScale.setTextColor(Color.parseColor("#333333"));
        root.addView(lblScale);

        // ── Scale mode buttons
        LinearLayout scaleRow = new LinearLayout(this);
        scaleRow.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams srP = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        srP.setMargins(0, 4, 0, 0);
        scaleRow.setLayoutParams(srP);

        String[] scaleModes = {"Fill", "Fit", "Stretch"};
        final int[] selectedScale = {0}; // 0=Fill, 1=Fit, 2=Stretch
        final Button[] scaleBtns = new Button[3];

        for (int i = 0; i < scaleModes.length; i++) {
            final int idx = i;
            Button sb = new Button(this);
            sb.setText(scaleModes[i]);
            sb.setTextSize(12);
            LinearLayout.LayoutParams sp = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
            sp.setMargins(0, 0, i < 2 ? 6 : 0, 0);
            sb.setLayoutParams(sp);
            sb.setBackgroundColor(i == 0 ? Color.parseColor("#1565C0") : Color.parseColor("#90CAF9"));
            sb.setTextColor(Color.WHITE);
            scaleBtns[i] = sb;
            sb.setOnClickListener(v -> {
                selectedScale[0] = idx;
                for (int j = 0; j < scaleBtns.length; j++) {
                    scaleBtns[j].setBackgroundColor(j == idx ? Color.parseColor("#1565C0") : Color.parseColor("#90CAF9"));
                }
            });
            scaleRow.addView(sb);
        }
        root.addView(scaleRow);

        builder.setView(root);

        final AlertDialog[] dialogRef = {null};

        // ── Gallery click
        btnGallery.setOnClickListener(v -> {
            pendingTextBgTarget = targetView;
            if (dialogRef[0] != null) dialogRef[0].dismiss();

            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(Intent.createChooser(intent, "BG Image Select"), REQUEST_TEXT_BG_IMAGE1);
        });

        // ── Remove BG click
        btnRemoveBg.setOnClickListener(v -> {
            // BG image remove — solid color background restore
            int storedColor = getStoredBackgroundColor(targetView);
            GradientDrawable gd = new GradientDrawable();
            gd.setColor(storedColor);
            Object borderTag = targetView.getTag(R.id.btn_add_sticker);
            int borderStyle = borderTag instanceof Integer ? (int) borderTag : 0;
            applyBorderStyle(gd, borderStyle);
            targetView.setBackground(gd);

            // Tag clear
            targetView.setTag(R.id.btn_sticker_gallery, "");

            exportToJson();
            if (dialogRef[0] != null) dialogRef[0].dismiss();
            Toast.makeText(this, "✅ BG Image remove થઈ ગઈ!", Toast.LENGTH_SHORT).show();
        });

        // ── Opacity change — live update on existing bg
        seekOpacity.setOnSeekBarChangeListener(new android.widget.SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(android.widget.SeekBar s, int progress, boolean fromUser) {
                if (!fromUser) return;
                lblOpacity.setText("Opacity: " + progress + "%");
                // Alpha apply to view's background drawable
                android.graphics.drawable.Drawable bg = targetView.getBackground();
                if (bg != null) {
                    bg.setAlpha((int) (255 * progress / 100f));
                }
            }

            @Override
            public void onStartTrackingTouch(android.widget.SeekBar s) {
            }

            @Override
            public void onStopTrackingTouch(android.widget.SeekBar s) {
                exportToJson();
            }
        });

        builder.setPositiveButton("Apply", (dialog, which) -> {
            // Opacity tag store
            targetView.setTag(R.id.tv_move_speed, seekOpacity.getProgress());
            exportToJson();
        });
        builder.setNegativeButton("Cancel", null);

        dialogRef[0] = builder.show();
    }


    private void applyTextBgImage(Uri imageUri, final StrokeTextView targetView) {
        if (imageUri == null || targetView == null) return;

        Toast.makeText(this, "Background set થઈ રહ્યો છે...", Toast.LENGTH_SHORT).show();

        Glide.with(this).asBitmap().load(imageUri).into(new com.bumptech.glide.request.target.CustomTarget<Bitmap>() {

            @Override
            public void onResourceReady(@NonNull Bitmap bitmap, @Nullable com.bumptech.glide.request.transition.Transition<? super Bitmap> transition) {

                runOnUiThread(() -> {
                    try {
                        // ── View size get
                        int vw = targetView.getWidth();
                        int vh = targetView.getHeight();

                        if (vw <= 0 || vh <= 0) {
                            vw = 300;
                            vh = 100;
                        }

                        // ── Scale bitmap to view size
                        Bitmap scaled = Bitmap.createScaledBitmap(bitmap, vw, vh, true);

                        // ── BitmapDrawable create
                        android.graphics.drawable.BitmapDrawable bitmapDrawable = new android.graphics.drawable.BitmapDrawable(getResources(), scaled);

                        // ── Border style maintain
                        // BitmapDrawable ने LayerDrawable
                        // wrap करीने border apply
                        Object borderTag = targetView.getTag(R.id.btn_add_sticker);
                        int borderStyle = borderTag instanceof Integer ? (int) borderTag : 0;

                        if (borderStyle > 0) {
                            // Border + BG image both show
                            GradientDrawable border = new GradientDrawable();
                            border.setColor(Color.TRANSPARENT);
                            applyBorderStyle(border, borderStyle);

                            android.graphics.drawable.LayerDrawable layered = new android.graphics.drawable.LayerDrawable(new android.graphics.drawable.Drawable[]{bitmapDrawable, border});
                            targetView.setBackground(layered);
                        } else {
                            targetView.setBackground(bitmapDrawable);
                        }

                        // ── URI tag store — save/load/remove
                        targetView.setTag(R.id.btn_sticker_gallery, imageUri.toString());

                        exportToJson();

                        Toast.makeText(MainActivity.this, "✅ Background set!", Toast.LENGTH_SHORT).show();

                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(MainActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onLoadCleared(@Nullable android.graphics.drawable.Drawable placeholder) {
            }

            @Override
            public void onLoadFailed(@Nullable android.graphics.drawable.Drawable errorDrawable) {
                Toast.makeText(MainActivity.this, "Image load failed", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private boolean hasTextBgImage(StrokeTextView tv) {
        Object tag = tv.getTag(R.id.btn_sticker_gallery);
        return tag != null && !tag.toString().trim().isEmpty();
    }

    /**
     * Move arrow buttons
     */
    private void bindMoveButtons(View cv, View targetView) {
        TextView btnUp = cv.findViewById(R.id.btn_sel_up);
        TextView btnDown = cv.findViewById(R.id.btn_sel_down);
        TextView btnLeft = cv.findViewById(R.id.btn_sel_left);
        TextView btnRight = cv.findViewById(R.id.btn_sel_right);

        if (btnUp != null) {
            btnUp.setOnClickListener(v -> moveSingleView(targetView, 0, -SEL_MOVE_STEP));
            btnUp.setOnLongClickListener(v -> {
                moveSingleView(targetView, 0, -SEL_MOVE_STEP * 5);
                return true;
            });
        }
        if (btnDown != null) {
            btnDown.setOnClickListener(v -> moveSingleView(targetView, 0, SEL_MOVE_STEP));
            btnDown.setOnLongClickListener(v -> {
                moveSingleView(targetView, 0, SEL_MOVE_STEP * 5);
                return true;
            });
        }
        if (btnLeft != null) {
            btnLeft.setOnClickListener(v -> moveSingleView(targetView, -SEL_MOVE_STEP, 0));
            btnLeft.setOnLongClickListener(v -> {
                moveSingleView(targetView, -SEL_MOVE_STEP * 5, 0);
                return true;
            });
        }
        if (btnRight != null) {
            btnRight.setOnClickListener(v -> moveSingleView(targetView, SEL_MOVE_STEP, 0));
            btnRight.setOnLongClickListener(v -> {
                moveSingleView(targetView, SEL_MOVE_STEP * 5, 0);
                return true;
            });
        }
    }


    /**
     * Drag handle setup — pure delta tracking, no getLocationOnScreen
     */
    private void setupDragHandle(View cv) {
        final float[] lastTX = {0};
        final float[] lastTY = {0};
        final boolean[] dragging = {false};

        View dragHandle = cv.findViewById(R.id.drag_handle_sel);
        if (dragHandle == null) return;

        dragHandle.setOnTouchListener((v2, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    lastTX[0] = event.getRawX();
                    lastTY[0] = event.getRawY();
                    dragging[0] = true;
                    return true;

                case MotionEvent.ACTION_MOVE:
                    if (!dragging[0]) return true;
                    int dx = (int)(event.getRawX() - lastTX[0]);
                    int dy = (int)(event.getRawY() - lastTY[0]);
                    lastTX[0] = event.getRawX();
                    lastTY[0] = event.getRawY();
                    selControlsLastX += dx;
                    selControlsLastY += dy;
                    if (selectionControlsPopup != null && selectionControlsPopup.isShowing()) {
                        selectionControlsPopup.update(selControlsLastX, selControlsLastY, -1, -1);
                    }
                    isSelControlsMoved = true;
                    return true;

                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    dragging[0] = false;
                    return true;
            }
            return false;
        });
    }


    // ─────────────────────────────────────────────────────────────
//  IMAGE SELECTION CONTROLS
//  Layout: layout_selection_controls_image.xml
//  NO text-related buttons — only Size, Move, Layer, Align
// ─────────────────────────────────────────────────────────────
    private void showSelectionControlsForImage(ImageView targetView) {

        try {
            if (selectionControlsPopup != null && selectionControlsPopup.isShowing()) {
                selectionControlsPopup.dismiss();
            }
        } catch (Exception e) {
            selectionControlsPopup = null;
        }

        LayoutInflater inflater = LayoutInflater.from(this);
        // ✅ IMAGE-specific XML
        View cv = inflater.inflate(R.layout.layout_selection_controls_image, null);

        selectionControlsPopup = new PopupWindow(cv, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, false);
        selectionControlsPopup.setOutsideTouchable(false);
        selectionControlsPopup.setTouchable(true);
        selectionControlsPopup.setBackgroundDrawable(new android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT));

        // ── Drag handle
        setupDragHandle(cv);

        // ── Save original image size
        android.view.ViewGroup.LayoutParams lp = targetView.getLayoutParams();
        selOriginalW = (lp != null && lp.width > 0) ? lp.width : targetView.getWidth();
        selOriginalH = (lp != null && lp.height > 0) ? lp.height : targetView.getHeight();

        // ── Size label shows W/H
        TextView tvLbl = cv.findViewById(R.id.tv_sel_size_label);
        if (tvLbl != null) {
            tvLbl.setText("W:" + selOriginalW + "  H:" + selOriginalH);
        }

        android.widget.SeekBar seekSize = cv.findViewById(R.id.seek_sel_size);

        if (seekSize != null) {
            seekSize.setOnSeekBarChangeListener(new android.widget.SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(android.widget.SeekBar s, int progress, boolean fromUser) {
                    if (!fromUser) return;
                    applySelSizeChange(targetView, progress, tvLbl);
                    // Update W/H label
                    if (tvLbl != null) {
                        android.view.ViewGroup.LayoutParams p = targetView.getLayoutParams();
                        if (p != null) tvLbl.setText("W:" + p.width + "  H:" + p.height);
                    }
                }

                @Override
                public void onStartTrackingTouch(android.widget.SeekBar s) {
                }

                @Override
                public void onStopTrackingTouch(android.widget.SeekBar s) {
                    exportToJson();
                }
            });
        }

        bindSizeButtons(cv, targetView, seekSize, tvLbl);

        // ── Move buttons
        bindMoveButtons(cv, targetView);

        // ── Layer buttons
        bindLayerButtons(cv, targetView);

        // ── Align buttons
        bindAlignButtons(cv, targetView);

        // ── Toggle rows
        bindToggleRows(cv);

        // ── Close
        TextView btnClose = cv.findViewById(R.id.btn_sel_close);
        if (btnClose != null) {
            btnClose.setOnClickListener(v -> dismissSelectionControls());
        }

        // ── Show popup
        showPopupAtSavedPosition(cv);
    }


    /**
     * Size +/- buttons
     */
    private void bindSizeButtons(View cv, View targetView, android.widget.SeekBar seekSize, TextView tvLbl) {

        TextView btnSzM = cv.findViewById(R.id.btn_sel_size_minus);
        TextView btnSzP = cv.findViewById(R.id.btn_sel_size_plus);
        LinearLayout panelArc = cv.findViewById(R.id.panel_arc_controls);

        if (btnSzM != null) {
            btnSzM.setOnClickListener(v -> {
                if (seekSize == null) return;
                int nv = Math.max(0, seekSize.getProgress() - 2);
                seekSize.setProgress(nv);
                applySelSizeChange(targetView, nv, tvLbl);
                exportToJson();
            });
            btnSzM.setOnLongClickListener(v -> {
                if (seekSize == null) return true;
                int nv = Math.max(0, seekSize.getProgress() - 5);
                seekSize.setProgress(nv);
                applySelSizeChange(targetView, nv, tvLbl);
                return true;
            });
        }
        if (btnSzP != null) {
            btnSzP.setOnClickListener(v -> {
                if (seekSize == null) return;
                int nv = Math.min(100, seekSize.getProgress() + 2);
                seekSize.setProgress(nv);
                applySelSizeChange(targetView, nv, tvLbl);
                exportToJson();
            });
            btnSzP.setOnLongClickListener(v -> {
                if (seekSize == null) return true;
                int nv = Math.min(100, seekSize.getProgress() + 5);
                seekSize.setProgress(nv);
                applySelSizeChange(targetView, nv, tvLbl);
                return true;
            });
        }
    }

    /**
     * Layer buttons
     */
    private void bindLayerButtons(View cv, View targetView) {
        View vFront = cv.findViewById(R.id.btn_pop_bring_front);
        if (vFront != null) vFront.setOnClickListener(v -> {
            bringViewToFront(targetView);
            if (currentStickerToolbarPopup != null) currentStickerToolbarPopup.dismiss();
        });

        View vLayerUp = cv.findViewById(R.id.btn_pop_layer_up);
        if (vLayerUp != null) vLayerUp.setOnClickListener(v -> {
            bringViewOneLayerUp(targetView);
            exportToJson();
            refreshLockedLayersPanel();
        });

        View vLayerDown = cv.findViewById(R.id.btn_pop_layer_down);
        if (vLayerDown != null) vLayerDown.setOnClickListener(v -> {
            sendViewOneLayerDown(targetView);
            exportToJson();
            refreshLockedLayersPanel();
        });

        View vBack = cv.findViewById(R.id.btn_pop_send_back);
        if (vBack != null) vBack.setOnClickListener(v -> {
            sendViewToBack(targetView);
            if (currentStickerToolbarPopup != null) currentStickerToolbarPopup.dismiss();
        });
    }


    /**
     * Align buttons
     */
    private void bindAlignButtons(View cv, View targetView) {
        TextView btnSelAlignLeft = cv.findViewById(R.id.btn_sel_align_left);
        TextView btnSelAlignCenterH = cv.findViewById(R.id.btn_sel_align_center_h);
        TextView btnSelAlignCenterH1 = cv.findViewById(R.id.btn_sel_align_center_h1);
        TextView btnSelAlignRight = cv.findViewById(R.id.btn_sel_align_right);
        TextView btnSelAlignTop = cv.findViewById(R.id.btn_sel_align_top);
        TextView btnSelAlignMiddle = cv.findViewById(R.id.btn_sel_align_middle);
        TextView btnSelAlignBottom = cv.findViewById(R.id.btn_sel_align_bottom);
        TextView btnSelDistH = cv.findViewById(R.id.btn_sel_distribute_h);
        TextView btnSelDistV = cv.findViewById(R.id.btn_sel_distribute_v);

        View.OnClickListener singleAlignListener = v -> {
            int id = v.getId();

            float canvasLeft = main_image_view.getX();
            float canvasTop = main_image_view.getY();
            float canvasRight = canvasLeft + main_image_view.getWidth();
            float canvasBottom = canvasTop + main_image_view.getHeight();
            float canvasCenterX = canvasLeft + main_image_view.getWidth() / 2f;
            float canvasCenterY = canvasTop + main_image_view.getHeight() / 2f;

            List<View> targets = new ArrayList<>();
            if (!selectedViews.isEmpty()) {
                targets.addAll(selectedViews);
            } else {
                targets.add(targetView);
            }

            if ((id == R.id.btn_sel_distribute_h || id == R.id.btn_sel_distribute_v) && targets.size() < 2) {
                Toast.makeText(this, "Distribute માટે 2+ elements select કરો", Toast.LENGTH_SHORT).show();
                return;
            }

            if (id == R.id.btn_sel_align_left) {
                for (View tv2 : targets) tv2.setX(canvasLeft);
            } else if (id == R.id.btn_sel_align_center_h) {
                for (View tv2 : targets)
                    tv2.setX(canvasCenterX - tv2.getWidth() / 2f);
            } else if (id == R.id.btn_sel_align_center_h1) {
                for (View tv2 : targets)
                    tv2.setX(canvasCenterX - tv2.getWidth() / 2f);
            } else if (id == R.id.btn_sel_align_right) {
                for (View tv2 : targets)
                    tv2.setX(canvasRight - tv2.getWidth());
            } else if (id == R.id.btn_sel_align_top) {
                for (View tv2 : targets) tv2.setY(canvasTop);
            } else if (id == R.id.btn_sel_align_middle) {
                for (View tv2 : targets)
                    tv2.setY(canvasCenterY - tv2.getHeight() / 2f);
            } else if (id == R.id.btn_sel_align_bottom) {
                for (View tv2 : targets)
                    tv2.setY(canvasBottom - tv2.getHeight());
            } else if (id == R.id.btn_sel_distribute_h) {
                targets.sort((a, b) -> Float.compare(a.getX(), b.getX()));
                float totalW = 0;
                for (View tv2 : targets) totalW += tv2.getWidth();
                float gap = (main_image_view.getWidth() - totalW) / (targets.size() - 1);
                float curX = canvasLeft;
                for (View tv2 : targets) {
                    tv2.setX(curX);
                    curX += tv2.getWidth() + gap;
                }
            } else if (id == R.id.btn_sel_distribute_v) {
                targets.sort((a, b) -> Float.compare(a.getY(), b.getY()));
                float totalH = 0;
                for (View tv2 : targets) totalH += tv2.getHeight();
                float gap = (main_image_view.getHeight() - totalH) / (targets.size() - 1);
                float curY = canvasTop;
                for (View tv2 : targets) {
                    tv2.setY(curY);
                    curY += tv2.getHeight() + gap;
                }
            }

            groupStartPositions.clear();
            for (View sel : selectedViews)
                groupStartPositions.add(new float[]{sel.getX(), sel.getY()});

            exportToJson();
        };

        if (btnSelAlignLeft != null) btnSelAlignLeft.setOnClickListener(singleAlignListener);
        if (btnSelAlignCenterH != null) btnSelAlignCenterH.setOnClickListener(singleAlignListener);
        if (btnSelAlignCenterH1 != null)
            btnSelAlignCenterH1.setOnClickListener(singleAlignListener);
        if (btnSelAlignRight != null) btnSelAlignRight.setOnClickListener(singleAlignListener);
        if (btnSelAlignTop != null) btnSelAlignTop.setOnClickListener(singleAlignListener);
        if (btnSelAlignMiddle != null) btnSelAlignMiddle.setOnClickListener(singleAlignListener);
        if (btnSelAlignBottom != null) btnSelAlignBottom.setOnClickListener(singleAlignListener);
        if (btnSelDistH != null) btnSelDistH.setOnClickListener(singleAlignListener);
        if (btnSelDistV != null) btnSelDistV.setOnClickListener(singleAlignListener);
    }



/*    private void bindToggleRows(View cv) {
        TextView btnToggleMove = cv.findViewById(R.id.btn_toggle_move);
        TextView btnToggleLayer = cv.findViewById(R.id.btn_toggle_layer);
        TextView btnToggleAlign = cv.findViewById(R.id.btn_toggle_align);

        LinearLayout rowMove = cv.findViewById(R.id.row_move_btns);
        LinearLayout rowLayer = cv.findViewById(R.id.row_layer_btns);
        HorizontalScrollView hsvAlign = cv.findViewById(R.id.hsv_align_row);

        if (btnToggleMove != null && rowMove != null) {
            btnToggleMove.setOnClickListener(v -> {
                boolean show = rowMove.getVisibility() != View.VISIBLE;
                rowMove.setVisibility(show ? View.VISIBLE : View.GONE);
                if (show) {
                    if (rowLayer != null) rowLayer.setVisibility(View.GONE);
                    if (hsvAlign != null) hsvAlign.setVisibility(View.GONE);
                }
                btnToggleMove.setAlpha(show ? 1.0f : 0.6f);
                if (btnToggleLayer != null) btnToggleLayer.setAlpha(0.6f);
                if (btnToggleAlign != null) btnToggleAlign.setAlpha(0.6f);
            });
        }

        if (btnToggleLayer != null && rowLayer != null) {
            btnToggleLayer.setOnClickListener(v -> {
                boolean show = rowLayer.getVisibility() != View.VISIBLE;
                rowLayer.setVisibility(show ? View.VISIBLE : View.GONE);
                if (show) {
                    if (rowMove != null) rowMove.setVisibility(View.GONE);
                    if (hsvAlign != null) hsvAlign.setVisibility(View.GONE);
                }
                btnToggleLayer.setAlpha(show ? 1.0f : 0.6f);
                if (btnToggleMove != null) btnToggleMove.setAlpha(0.6f);
                if (btnToggleAlign != null) btnToggleAlign.setAlpha(0.6f);
            });
        }

        if (btnToggleAlign != null && hsvAlign != null) {
            btnToggleAlign.setOnClickListener(v -> {
                boolean show = hsvAlign.getVisibility() != View.VISIBLE;
                hsvAlign.setVisibility(show ? View.VISIBLE : View.GONE);
                if (show) {
                    if (rowMove != null) rowMove.setVisibility(View.GONE);
                    if (rowLayer != null) rowLayer.setVisibility(View.GONE);
                }
                btnToggleAlign.setAlpha(show ? 1.0f : 0.6f);
                if (btnToggleMove != null) btnToggleMove.setAlpha(0.6f);
                if (btnToggleLayer != null) btnToggleLayer.setAlpha(0.6f);
            });
        }
    }*/

    private void bindToggleRows(View cv) {
        TextView btnToggleMove = cv.findViewById(R.id.btn_toggle_move);
        TextView btnToggleLayer = cv.findViewById(R.id.btn_toggle_layer);
        TextView btnToggleAlign = cv.findViewById(R.id.btn_toggle_align);
        LinearLayout rowMove = cv.findViewById(R.id.row_move_btns);
        LinearLayout rowLayer = cv.findViewById(R.id.row_layer_btns);
        HorizontalScrollView hsvAlign = cv.findViewById(R.id.hsv_align_row);

        if (btnToggleMove == null || rowMove == null) return;

        if (btnToggleMove != null && rowMove != null) {
            btnToggleMove.setOnClickListener(v -> {
                boolean show = rowMove.getVisibility() != View.VISIBLE;
                rowMove.setVisibility(show ? View.VISIBLE : View.GONE);
                if (show && rowLayer != null) rowLayer.setVisibility(View.GONE);
                if (show && hsvAlign != null) hsvAlign.setVisibility(View.GONE);
            });
        }

        if (btnToggleAlign != null && hsvAlign != null) {
            btnToggleAlign.setOnClickListener(v -> {
                boolean show = hsvAlign.getVisibility() != View.VISIBLE;
                hsvAlign.setVisibility(show ? View.VISIBLE : View.GONE);
                if (show && rowMove != null) rowMove.setVisibility(View.GONE);
                if (show && rowLayer != null) rowLayer.setVisibility(View.GONE);
            });
        }
    }


    /**
     * Show popup at last saved position (or bottom-center first time)
     */
    private void showPopupAtSavedPosition(View cv) {
        if (isSelControlsMoved) {
            selectionControlsPopup.showAtLocation(mainLayout, Gravity.TOP | Gravity.LEFT, selControlsLastX, selControlsLastY);
        } else {
            // First time — measure karvi pachhi bottom-center ma TOP|LEFT coordinates thi show
            cv.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
            int popupW = cv.getMeasuredWidth();
            int popupH = cv.getMeasuredHeight();
            selControlsLastX = Math.max(0, (mainLayout.getWidth() - popupW) / 2);
            selControlsLastY = Math.max(0, mainLayout.getHeight() - popupH - 25);
            selectionControlsPopup.showAtLocation(mainLayout, Gravity.TOP | Gravity.LEFT, selControlsLastX, selControlsLastY);
        }
    }


    private void showReplaceStickerDialog(final ImageView targetSticker, final String catId) {

        // Loading dialog
        AlertDialog loadingDialog = new AlertDialog.Builder(this).setMessage("Images load થઈ રહી છે...").setCancelable(false).create();
        loadingDialog.show();

        // API call — same CID ની images fetch
        new Thread(() -> {
            try {
                // ── API Request build (same as invite_sticker_lis_common)
                invite_Methods methods = new invite_Methods(this);
                RequestBody requestBody = methods.getAPIRequest(invite_AppConstants.METHOD_IMAGE_PHOTOSTICKER, 1,   // page 1
                        "", "", "", catId,  // catId pass
                        "", "", "", "", "", "", "", "", "", invite_AppConstants.itemUser.getId(), "", null);

                String json = invite_JSONParser.okhttpPost(invite_AppConstants.SERVER_URL, requestBody);

                ArrayList<String> imageUrls = new ArrayList<>();

                JSONObject jOb = new JSONObject(json);
                JSONArray jsonArray = jOb.getJSONArray(invite_AppConstants.TAG_ROOT);

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject obj = jsonArray.getJSONObject(i);
                    if (!obj.has(invite_AppConstants.TAG_SUCCESS)) {
                        String imgUrl = obj.getString(invite_AppConstants.TAG_QUOTES_IMAGE_BIG1);
                        if (!imgUrl.isEmpty()) {
                            imageUrls.add(imgUrl);
                        }
                    }
                }

                runOnUiThread(() -> {
                    loadingDialog.dismiss();
                    if (imageUrls.isEmpty()) {
                        Toast.makeText(this, "આ category માં images નથી", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    showReplacePickerDialog(targetSticker, catId, imageUrls);
                });

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    loadingDialog.dismiss();
                    Toast.makeText(this, "Images load કરવામાં ભૂલ: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }

    private void showReplacePickerDialog(final ImageView targetSticker, final String catId, final ArrayList<String> imageUrls) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Sticker Replace કરો");

        // RecyclerView setup
        RecyclerView recyclerView = new RecyclerView(this);
        recyclerView.setPadding(16, 16, 16, 16);

        androidx.recyclerview.widget.GridLayoutManager gridLayout = new androidx.recyclerview.widget.GridLayoutManager(this, 3);
        recyclerView.setLayoutManager(gridLayout);

        // Simple Image Adapter
        RecyclerView.Adapter<RecyclerView.ViewHolder> adapter = new RecyclerView.Adapter<RecyclerView.ViewHolder>() {

            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                ImageView iv = new ImageView(MainActivity.this);
                int size = (getResources().getDisplayMetrics().widthPixels - 120) / 3;
                RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(size, size);
                lp.setMargins(8, 8, 8, 8);
                iv.setLayoutParams(lp);
                iv.setScaleType(ImageView.ScaleType.FIT_XY);

                GradientDrawable border = new GradientDrawable();
                border.setColor(Color.WHITE);
                border.setStroke(1, Color.LTGRAY);
                iv.setBackground(border);

                return new RecyclerView.ViewHolder(iv) {
                };
            }

            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
                ImageView iv = (ImageView) holder.itemView;
                String url = imageUrls.get(position);

                Glide.with(MainActivity.this).load(url).placeholder(android.R.drawable.ic_menu_gallery).into(iv);
            }

            @Override
            public int getItemCount() {
                return imageUrls.size();
            }
        };

        recyclerView.setAdapter(adapter);

        AlertDialog dialog = builder.setView(recyclerView).setNegativeButton("Cancel", null).create();

        // Item click — sticker replace
        recyclerView.addOnItemTouchListener(new invite_AppConstants.RecyclerTouchListener(this, recyclerView, new invite_AppConstants.RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                String newUrl = imageUrls.get(position);

                // ✅ Sticker replace — same position, size, rotation
                Glide.with(MainActivity.this).load(newUrl).into(new CustomTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable com.bumptech.glide.request.transition.Transition<? super Drawable> transition) {
                        targetSticker.setImageDrawable(resource);
                        // ✅ URL અને CID update
                        targetSticker.setTag(R.id.btn_set_background, newUrl);
                        targetSticker.setTag(R.id.btn_add_sticker, catId);
                        exportToJson(); // ✅ JSON update
                        Toast.makeText(MainActivity.this, "Sticker replace થઈ ગયો!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                    }
                });

                dialog.dismiss();
            }

            @Override
            public void onLongClick(View view, int position) {
            }
        }));

        dialog.show();
    }

    private void toggleLock(View view) {
        if (lockedViews.contains(view)) {
            // ── Unlock
            lockedViews.remove(view);

            view.setAlpha(1.0f);

            // ✅ Cast fix
            if (view instanceof ImageView) {
                ((ImageView) view).setColorFilter(null);
            }

            // ── Touch listener re-enable
            if (view instanceof StrokeTextView) {
                applyTouchListener((StrokeTextView) view);
            } else if (view instanceof ImageView) {
                applyTouchListenerForSticker((ImageView) view);
            }

            Toast.makeText(this, "🔓 Unlock થઈ ગયો", Toast.LENGTH_SHORT).show();

        } else {
            // ── Lock
            lockedViews.add(view);
            view.setAlpha(0.85f);

            view.setOnTouchListener((v, event) -> {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    showLockPopup(view);
                }
                return true;
            });

            Toast.makeText(this, "🔒 Lock થઈ ગયો", Toast.LENGTH_SHORT).show();
        }

        updateLockIcon(view);
        exportToJson();

        refreshLockedLayersPanel();
    }


    private void updateLockIcon(View view) {
        boolean isLocked = lockedViews.contains(view);

        if (isLocked) {
            // ── Lock overlay — view ઉપર 🔒 show
            view.setTag(R.id.btn_ms_select_all, "LOCKED"); // lock tag

            // Border change — red dashed
            if (view instanceof StrokeTextView) {
                StrokeTextView tv = (StrokeTextView) view;
                GradientDrawable gd = new GradientDrawable();
                gd.setColor(getStoredBackgroundColor(tv));
                gd.setStroke(3, Color.RED, 10f, 5f); // dashed red
                gd.setCornerRadius(8f);
                tv.setBackground(gd);
            } else if (view instanceof ImageView) {
                GradientDrawable gd = new GradientDrawable();
                gd.setColor(Color.TRANSPARENT);
                gd.setStroke(3, Color.RED, 10f, 5f);
                view.setBackground(gd);
            }
        } else {
            // ── Unlock — border restore
            view.setTag(R.id.btn_ms_select_all, null);
            view.setAlpha(1.0f);

            if (view instanceof StrokeTextView) {
                restoreTextBorder((StrokeTextView) view);
            } else {
                view.setBackground(null);
                view.setPadding(0, 0, 0, 0);
            }
        }
    }

    private void showLockPopup(View lockedView) {
        new android.app.AlertDialog.Builder(this).setTitle("🔒 Locked").setMessage("આ item locked છે. Unlock કરવો?").setPositiveButton("🔓 Unlock", (dialog, which) -> {
            toggleLock(lockedView);
        }).setNegativeButton("Cancel", null).show();
    }


    private void showStickerToolbar(final android.widget.ImageView targetSticker) {

        Object _checkTag = targetSticker.getTag(R.id.btn_set_background);
        if ("FRAMED_IMAGE".equals(_checkTag)) {
            showFrameImageControlsPopup(targetSticker);
            return;
        }

        try {
            if (currentStickerToolbarPopup != null && currentStickerToolbarPopup.isShowing()) {
                currentStickerToolbarPopup.dismiss();
            }
        } catch (Exception e) {
            currentStickerToolbarPopup = null;
        }


        //showSelectionControls(targetSticker);

        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        // તમારા ટેક્સ્ટ ટૂલબાર વાળું જ XML વાપરો
        View popupView = inflater.inflate(R.layout.layout_image_toolbar, null);

        currentStickerToolbarPopup = new PopupWindow(popupView, RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT, true  // ✅ fix
        );

        currentStickerToolbarPopup.setOutsideTouchable(true); // ✅
        currentStickerToolbarPopup.setBackgroundDrawable(      // ✅ mandatory
                new android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT));
        currentStickerToolbarPopup.setTouchable(true);

// ✅ Dismiss listener — selectionControls પણ dismiss
        currentStickerToolbarPopup.setOnDismissListener(() -> {
            try {
                if (selectionControlsPopup != null && selectionControlsPopup.isShowing()) {
                    currentStickerToolbarPopup = null;
                    //selectionControlsPopup.dismiss();
                }
            } catch (Exception e) {
                selectionControlsPopup = null;
            }
            currentStickerToolbarPopup = null;
        });


        // ── Lock button
        TextView btnLock = popupView.findViewById(R.id.btn_pop_lock);
        if (btnLock != null) {
            // ── Current state show
            boolean isLocked = lockedViews.contains(targetSticker);
            btnLock.setText(isLocked ? "🔒\nLocked" : "🔓\nLock");
            btnLock.setBackgroundColor(isLocked ? Color.parseColor("#FFCDD2")  // locked = red tint
                    : Color.parseColor("#E8F5E9")); // unlocked = green tint

            btnLock.setOnClickListener(v -> {
                currentStickerToolbarPopup.dismiss();
                toggleLock(targetSticker);
            });
        }

        popupView.findViewById(R.id.btnSelectPhoto).setOnClickListener(v -> {
            if (currentStickerToolbarPopup != null
                    && currentStickerToolbarPopup.isShowing()) {
                currentStickerToolbarPopup.dismiss();
            }
            currentFrameTargetSticker = targetSticker;
            Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
            galleryIntent.setType("image/*");
            startActivityForResult(
                    Intent.createChooser(galleryIntent, "Photo Select"),
                    REQUEST_FRAME_PHOTO_ADJUST);
        });


        // ✅ FRAMED_IMAGE check — color button show/hide
        Object uriTag = targetSticker.getTag(R.id.btn_set_background);
        boolean isFramedImage = "FRAMED_IMAGE".equals(uriTag);

        // ── Select Photo button
        View btnSelectPhoto = popupView.findViewById(R.id.btn_pop_select_photo);
        if (btnSelectPhoto != null) {
            btnSelectPhoto.setVisibility(isFramedImage ? View.VISIBLE : View.GONE);
            btnSelectPhoto.setOnClickListener(v -> {
                if (currentStickerToolbarPopup != null
                        && currentStickerToolbarPopup.isShowing()) {
                    currentStickerToolbarPopup.dismiss();
                }
                currentFrameTargetSticker = targetSticker;
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(
                        Intent.createChooser(galleryIntent, "Photo Select"),
                        REQUEST_FRAME_PHOTO_ADJUST);
            });
        }

        View btnColorChange = popupView.findViewById(R.id.btn_pop_frame_color);
        if (btnColorChange != null) {
            btnColorChange.setVisibility(isFramedImage ? View.VISIBLE : View.GONE);

            btnColorChange.setOnClickListener(v -> {
                currentStickerToolbarPopup.dismiss();
                showFrameColorChangeDialog(targetSticker);
            });
        }

        View btnFreehand = popupView.findViewById(R.id.btn_pop_freehand_crop);
        if (btnFreehand != null) {
            btnFreehand.setVisibility(isFramedImage ? View.GONE : View.VISIBLE);

            btnFreehand.setOnClickListener(v -> {
                currentStickerToolbarPopup.dismiss();
                openFreehandCrop(targetSticker);
            });
        }


        View btnCrop = popupView.findViewById(R.id.btn_pop_crop);
        if (btnCrop != null) {
            // FRAMED_IMAGE માટે hide
            btnCrop.setVisibility(isFramedImage ? View.GONE : View.VISIBLE);

            btnCrop.setOnClickListener(v -> {
                currentStickerToolbarPopup.dismiss();
                showCropOption(targetSticker);
            });
        }


        popupView.findViewById(R.id.btn_pop_replace_sticker).setOnClickListener(v -> {
            currentStickerToolbarPopup.dismiss();

            Object catTag = targetSticker.getTag(R.id.btn_add_sticker);
            String catId = (catTag != null) ? catTag.toString() : "";
            Log.println(ASSERT, "#cid", catId);
            if (catId.isEmpty()) {
                Toast.makeText(this, "આ sticker માટે category ID નથી", Toast.LENGTH_SHORT).show();
                return;
            }

            showReplaceStickerDialog(targetSticker, catId);
        });

        // ૩. ડીલીટ કરવા માટે
        popupView.findViewById(R.id.btn_pop_delete).setOnClickListener(v -> {

            try {
                JSONObject sObj = new JSONObject();

                // ✅ uriTag → delUriTag (rename)
                Object delUriTag = targetSticker.getTag(R.id.btn_set_background);
                String delUriStr = delUriTag != null ? delUriTag.toString() : "";

                sObj.put("uri", delUriStr);

                Object delCatTag = targetSticker.getTag(R.id.btn_add_sticker);
                sObj.put("catid", delCatTag != null ? delCatTag.toString() : "");

                Object delLocTag = targetSticker.getTag(R.id.btn_location);
                if (delLocTag != null) sObj.put("mapUrl", delLocTag.toString());

                float sw = mainLayout.getWidth();
                float sh = mainLayout.getHeight();
                sObj.put("xPercent", (targetSticker.getX() / sw) * 100);
                sObj.put("yPercent", (targetSticker.getY() / sh) * 100);

                ViewGroup.LayoutParams lp = targetSticker.getLayoutParams();
                int w = lp != null && lp.width > 0 ? lp.width : targetSticker.getWidth();
                int h = lp != null && lp.height > 0 ? lp.height : targetSticker.getHeight();
                sObj.put("widthPercent", ((float) w / sw) * 100);
                sObj.put("heightPercent", ((float) h / sh) * 100);
                sObj.put("rotation", targetSticker.getRotation());
                sObj.put("scaleX", targetSticker.getScaleX());
                sObj.put("scaleY", targetSticker.getScaleY());
                sObj.put("isFramedImage", "FRAMED_IMAGE".equals(delUriStr));

                if ("FRAMED_IMAGE".equals(delUriStr)) {
                    Object topTag = targetSticker.getTag(R.id.btn_add_sticker);
                    Object maskTag = targetSticker.getTag(R.id.btn_location);
                    Object colorTag = targetSticker.getTag(R.id.seek_multi_size);
                    sObj.put("frameTopUrl", topTag != null ? topTag.toString() : "");
                    sObj.put("frameMaskUrl", maskTag != null ? maskTag.toString() : "");
                    sObj.put("frameColor", colorTag instanceof Integer ? (int) colorTag : Color.TRANSPARENT);
                }

                deletedStickersList.add(sObj);

            } catch (Exception e) {
                e.printStackTrace();
            }

            mainLayout.removeView(targetSticker);
            currentStickerToolbarPopup.dismiss();
            currentlySelectedView = null;
            exportToJson();
        });

        // પોપઅપ ક્યાં બતાવવું તેનું લોકેશન
        int[] location = new int[2];
        targetSticker.getLocationOnScreen(location);
        currentStickerToolbarPopup.showAtLocation(targetSticker, Gravity.NO_GRAVITY, location[0], location[1] - 160);

        showSelectionControlsForImage(targetSticker);
    }


    private void showFrameColorChangeDialog(final ImageView targetSticker) {

        // ── Tags થી data retrieve
        Object maskTag = targetSticker.getTag(R.id.btn_location);
        Object topTag = targetSticker.getTag(R.id.btn_add_sticker);
        Object colorTag = targetSticker.getTag(R.id.seek_multi_size);

        String maskUrl = maskTag != null ? maskTag.toString() : "";
        String topUrl = topTag != null ? topTag.toString() : "";
        int currentColor = colorTag instanceof Integer ? (int) colorTag : Color.TRANSPARENT;

        // ── Check — FRAMED_IMAGE છે?
        if (maskUrl.isEmpty() && topUrl.isEmpty()) {
            Toast.makeText(this, "આ frame image નથી", Toast.LENGTH_SHORT).show();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Frame Color બદલો");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(32, 24, 32, 24);

        // ── Color Preview
        final View colorPreview = new View(this);
        colorPreview.setBackgroundColor(currentColor == Color.TRANSPARENT ? Color.parseColor("#F5F5F5") : currentColor);
        LinearLayout.LayoutParams cpP = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 60);
        cpP.setMargins(0, 0, 0, 12);
        colorPreview.setLayoutParams(cpP);
        layout.addView(colorPreview);

        // ── Label
        final TextView lblSelected = new TextView(this);
        lblSelected.setText("Current: " + (currentColor == Color.TRANSPARENT ? "No Color" : "Custom"));
        lblSelected.setTextSize(13);
        lblSelected.setPadding(0, 0, 0, 8);
        layout.addView(lblSelected);

        // ── Quick Colors
        final int[] selectedColor = {currentColor};

        String[] colorNames = {"None", "Red", "Green", "Blue", "Yellow", "Orange", "Pink", "Purple", "White", "Black"};
        int[] colors = {Color.TRANSPARENT, Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW, Color.rgb(255, 165, 0), Color.rgb(255, 105, 180), Color.rgb(128, 0, 128), Color.WHITE, Color.BLACK};

        android.widget.HorizontalScrollView hScroll = new android.widget.HorizontalScrollView(this);
        LinearLayout colorRow = new LinearLayout(this);
        colorRow.setOrientation(LinearLayout.HORIZONTAL);
        colorRow.setPadding(4, 4, 4, 4);

        for (int i = 0; i < colors.length; i++) {
            final int c = colors[i];
            final String name = colorNames[i];

            TextView btn = new TextView(this);
            btn.setGravity(Gravity.CENTER);
            btn.setTextSize(10);

            GradientDrawable gd = new GradientDrawable();
            if (c == Color.TRANSPARENT) {
                gd.setColor(Color.WHITE);
                btn.setText("✕");
                btn.setTextColor(Color.RED);
            } else {
                gd.setColor(c);
                btn.setText(name);
                btn.setTextColor(c == Color.WHITE || c == Color.YELLOW ? Color.BLACK : Color.WHITE);
            }
            gd.setStroke(2, Color.GRAY);
            gd.setCornerRadius(6f);
            btn.setBackground(gd);

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(80, 80);
            lp.setMargins(4, 0, 4, 0);
            btn.setLayoutParams(lp);

            btn.setOnClickListener(v -> {
                selectedColor[0] = c;
                colorPreview.setBackgroundColor(c == Color.TRANSPARENT ? Color.parseColor("#F5F5F5") : c);
                lblSelected.setText("Selected: " + name);
            });

            colorRow.addView(btn);
        }

        // ── Custom Color
        TextView btnCustom = new TextView(this);
        btnCustom.setText("🎨");
        btnCustom.setTextSize(20);
        btnCustom.setGravity(Gravity.CENTER);
        GradientDrawable customGd = new GradientDrawable();
        customGd.setColor(Color.parseColor("#E8EAF6"));
        customGd.setStroke(2, Color.parseColor("#3F51B5"));
        customGd.setCornerRadius(6f);
        btnCustom.setBackground(customGd);
        LinearLayout.LayoutParams cP = new LinearLayout.LayoutParams(80, 80);
        cP.setMargins(4, 0, 4, 0);
        btnCustom.setLayoutParams(cP);
        btnCustom.setOnClickListener(v -> showColorPickerPopup(
                        selectedColor[0] == Color.TRANSPARENT ? Color.RED : selectedColor[0],
                        color -> {
                            selectedColor[0] = color;
                colorPreview.setBackgroundColor(color);
                lblSelected.setText("Selected: Custom");
                        }));
        colorRow.addView(btnCustom);

        hScroll.addView(colorRow);
        layout.addView(hScroll);

        builder.setView(layout);

        // ── Apply
        builder.setPositiveButton("Apply", (dialog, which) -> {
            applyFrameColorChange(targetSticker, maskUrl, topUrl, selectedColor[0]);
        });

        builder.setNeutralButton("Remove Color", (dialog, which) -> {
            applyFrameColorChange(targetSticker, maskUrl, topUrl, Color.TRANSPARENT);
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void applyFrameColorChange(final ImageView targetSticker, final String maskUrl, final String topUrl, final int newColor) {

        Toast.makeText(this, "Color apply થઈ રહ્યો છે...", Toast.LENGTH_SHORT).show();

        // ✅ Sticker ની current bitmap retrieve
        targetSticker.setDrawingCacheEnabled(true);
        targetSticker.buildDrawingCache();

        // User image — sticker ની current bitmap (without frame)
        // Tag માંથી original user image URI
        // NOTE: FRAMED_IMAGE tag છે — original user image નથી
        // Solution: mask image load, top image load, re-merge

        // ── mask load
        Glide.with(this).asBitmap().load(maskUrl).into(new CustomTarget<Bitmap>() {
            @Override
            public void onResourceReady(@NonNull Bitmap maskBitmap, @Nullable com.bumptech.glide.request.transition.Transition<? super Bitmap> transition) {

                int w = maskBitmap.getWidth();
                int h = maskBitmap.getHeight();

                // ── Current sticker bitmap = user+mask already merged
                // Top image reload કરીને re-apply
                Glide.with(MainActivity.this).asBitmap().load(topUrl).into(new CustomTarget<Bitmap>(w, h) {
                    @Override
                    public void onResourceReady(@NonNull Bitmap topBitmap, @Nullable com.bumptech.glide.request.transition.Transition<? super Bitmap> transition) {

                        // ── Current sticker drawing = user+mask bitmap
                        targetSticker.setDrawingCacheEnabled(true);
                        Bitmap currentBitmap = Bitmap.createBitmap(targetSticker.getDrawingCache());
                        targetSticker.setDrawingCacheEnabled(false);

                        // ── New result — current bitmap ઉપર tinted top
                        Bitmap result = Bitmap.createBitmap(currentBitmap.getWidth(), currentBitmap.getHeight(), Bitmap.Config.ARGB_8888);
                        Canvas canvas = new Canvas(result);
                        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);

                        // Current (user+mask) draw
                        canvas.drawBitmap(currentBitmap, 0, 0, paint);

                        // Top with new color
                        Bitmap topScaled = Bitmap.createScaledBitmap(topBitmap, currentBitmap.getWidth(), currentBitmap.getHeight(), true);

                        if (newColor != Color.TRANSPARENT) {
                            Bitmap tinted = applyColorTint(topScaled, newColor);
                            canvas.drawBitmap(tinted, 0, 0, paint);
                            tinted.recycle();
                        } else {
                            canvas.drawBitmap(topScaled, 0, 0, paint);
                        }

                        topScaled.recycle();

                        // ✅ Update sticker
                        targetSticker.setImageBitmap(result);

                        // ✅ Color tag update
                        targetSticker.setTag(R.id.seek_multi_size, newColor);

                        exportToJson();
                        Toast.makeText(MainActivity.this, "✅ Color change થઈ ગયો!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable p) {
                    }
                });
            }

            @Override
            public void onLoadCleared(@Nullable Drawable p) {
            }

            @Override
            public void onLoadFailed(@Nullable Drawable e) {
                Toast.makeText(MainActivity.this, "Frame load failed", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void saveAllPagesAsImages() {
        // હાલના પેજને પહેલા સેવ કરી લો
        saveCurrentPage();

        Toast.makeText(this, "બધા પેજ સેવ થઈ રહ્યા છે...", Toast.LENGTH_SHORT).show();

        final android.os.Handler handler = new android.os.Handler();

        // લૂપ ચલાવો જેટલા પેજ હોય તેટલી વાર
        for (int i = 0; i < allPagesData.size(); i++) {
            final int index = i;

            // દરેક પેજ લોડ થવા માટે થોડો સમય આપવો (૧.૫ સેકન્ડ)
            handler.postDelayed(() -> {
                currentPageIndex = index;
                loadPageData(allPagesData.get(index));
                updatePageIndicator();

                // લેઆઉટ પૂરેપૂરું ડ્રો થાય તે માટે થોભો
                mainLayout.postDelayed(() -> {
                    captureAndSaveLayout(); // ઈમેજ સેવ કરવાનું ફંક્શન

                    if (index == allPagesData.size() - 1) {
                        Toast.makeText(MainActivity.this, "કુલ " + allPagesData.size() + " પેજ સેવ થયા!", Toast.LENGTH_LONG).show();
                    }
                }, 600); // ૬૦૦ મિલિસેકન્ડ રેન્ડરિંગ માટે

            }, i * 2000); // દરેક પેજ વચ્ચે ૨ સેકન્ડનો ગેપ (Glide લોડિંગ માટે સુરક્ષિત)
        }
    }


    private void captureAndSaveLayout() {
        try {
            // ૧. આખા મેઈન લેઆઉટનો પહેલા Bitmap બનાવો
            mainLayout.setDrawingCacheEnabled(true);
            mainLayout.buildDrawingCache();
            android.graphics.Bitmap fullBitmap = android.graphics.Bitmap.createBitmap(mainLayout.getDrawingCache());
            mainLayout.setDrawingCacheEnabled(false);

            // ૨. main_image_view ની સાઈઝ અને પોઝિશન મેળવો
            int width = main_image_view.getWidth();
            int height = main_image_view.getHeight();
            int x = (int) main_image_view.getX();
            int y = (int) main_image_view.getY();

            // ૩. માત્ર Image View જેટલો જ ભાગ Crop કરો
            // આનાથી વધારાની ખાલી જગ્યા સેવ નહીં થાય
            android.graphics.Bitmap croppedBitmap = android.graphics.Bitmap.createBitmap(fullBitmap, x, y, width, height);

            // ૪. ફાઈલનું નામ
            String fileName = "Card_Page_" + (currentPageIndex + 1) + "_" + System.currentTimeMillis() + ".jpg";

            // ૫. સેવ કરવાનું લોકેશન (Pictures ફોલ્ડર)
            File storageDir = new File(android.os.Environment.getExternalStoragePublicDirectory(android.os.Environment.DIRECTORY_PICTURES), "MyCardDesigns");

            if (!storageDir.exists()) storageDir.mkdirs();

            File imageFile = new File(storageDir, fileName);
            FileOutputStream fos = new FileOutputStream(imageFile);

            // Quality ૧૦૦ રાખો જેથી પ્રિન્ટિંગમાં સારું આવે
            croppedBitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();

            // ૬. ગેલેરીમાં બતાવવા માટે સ્કેન કરો
            android.media.MediaScannerConnection.scanFile(this, new String[]{imageFile.getAbsolutePath()}, new String[]{"image/jpeg"}, null);

            // મેમરી ખાલી કરવા માટે
            fullBitmap.recycle();
            // existing code પછી — fos.close(); ની નીચે

            ImageFileManager.saveImage(MainActivity.this, fileName, imageFile.getAbsolutePath());


        } catch (Exception e) {
            e.printStackTrace();
            Log.e("SaveError", "ઈમેજ સેવ કરવામાં ભૂલ: " + e.getMessage());
        }
    }


    private void showSelectionControls(View targetView) {

        try {
            if (selectionControlsPopup != null && selectionControlsPopup.isShowing()) {
                selectionControlsPopup.dismiss();
            }
        } catch (Exception e) {
            selectionControlsPopup = null;
        }

        LayoutInflater inflater = LayoutInflater.from(this);
        View cv = inflater.inflate(R.layout.layout_selection_controls, null);

        selectionControlsPopup = new PopupWindow(cv, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, false);  // ✅ false — drag work કરે
        selectionControlsPopup.setOutsideTouchable(false);
        selectionControlsPopup.setTouchable(true);
        selectionControlsPopup.setBackgroundDrawable(new android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT));

        // ── Drag — popup move
        final int[] popupX = {selControlsLastX};
        final int[] popupY = {selControlsLastY};
        final float[] lastTX = {0};
        final float[] lastTY = {0};

        // ── Drag handle view
        View dragHandle = cv.findViewById(R.id.drag_handle_sel);

        if (dragHandle != null) {
            dragHandle.setOnTouchListener((v2, event) -> {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        lastTX[0] = event.getRawX();
                        lastTY[0] = event.getRawY();
                        return true;

                    case MotionEvent.ACTION_MOVE:
                        float dx = event.getRawX() - lastTX[0];
                        float dy = event.getRawY() - lastTY[0];

                        popupX[0] += (int) dx;
                        popupY[0] += (int) dy;

                        lastTX[0] = event.getRawX();
                        lastTY[0] = event.getRawY();

                        selectionControlsPopup.update(popupX[0], popupY[0], -1, -1);

                        // ✅ Last moved position save
                        selControlsLastX = popupX[0];
                        selControlsLastY = popupY[0];

                        return true;
                }
                return false;
            });
        }

        // ── Size views
        android.widget.SeekBar seekSize = cv.findViewById(R.id.seek_sel_size);
        TextView tvLbl = cv.findViewById(R.id.tv_sel_size_label);


        // ✅ નવો code — TextView cast
        TextView btnSzM = cv.findViewById(R.id.btn_sel_size_minus);
        TextView btnSzP = cv.findViewById(R.id.btn_sel_size_plus);
        TextView btnUp = cv.findViewById(R.id.btn_sel_up);
        TextView btnDown = cv.findViewById(R.id.btn_sel_down);
        TextView btnLeft = cv.findViewById(R.id.btn_sel_left);
        TextView btnRight = cv.findViewById(R.id.btn_sel_right);
        TextView btnEdit = cv.findViewById(R.id.btn_sel_edit_text);

        // ── Align buttons bind
        TextView btnSelAlignLeft = cv.findViewById(R.id.btn_sel_align_left);
        TextView btnSelAlignCenterH = cv.findViewById(R.id.btn_sel_align_center_h);
        TextView btnSelAlignRight = cv.findViewById(R.id.btn_sel_align_right);
        TextView btnSelAlignTop = cv.findViewById(R.id.btn_sel_align_top);
        TextView btnSelAlignMiddle = cv.findViewById(R.id.btn_sel_align_middle);
        TextView btnSelAlignBottom = cv.findViewById(R.id.btn_sel_align_bottom);
        TextView btnSelDistH = cv.findViewById(R.id.btn_sel_distribute_h);
        TextView btnSelDistV = cv.findViewById(R.id.btn_sel_distribute_v);
// ── Toggle buttons
        TextView btnToggleMove = cv.findViewById(R.id.btn_toggle_move);
        TextView btnToggleLayer = cv.findViewById(R.id.btn_toggle_layer);
        TextView btnToggleAlign = cv.findViewById(R.id.btn_toggle_align);

        LinearLayout rowMove = cv.findViewById(R.id.row_move_btns);
        LinearLayout rowLayer = cv.findViewById(R.id.row_layer_btns);
        HorizontalScrollView hsvAlign = cv.findViewById(R.id.hsv_align_row);

        // ── Bring Front
        View vFront = cv.findViewById(R.id.btn_pop_bring_front);
        if (vFront != null) vFront.setOnClickListener(v -> {
            bringViewToFront(cv);
            if (currentStickerToolbarPopup != null) currentStickerToolbarPopup.dismiss();
        });

// ── Layer Up
        View vLayerUp = cv.findViewById(R.id.btn_pop_layer_up);
        if (vLayerUp != null) vLayerUp.setOnClickListener(v -> {
            bringViewOneLayerUp(cv);
            exportToJson();
            refreshLockedLayersPanel();
            // dismiss optional — up/down પછી toolbar રહે
        });

// ── Layer Down
        View vLayerDown = cv.findViewById(R.id.btn_pop_layer_down);
        if (vLayerDown != null) vLayerDown.setOnClickListener(v -> {
            sendViewOneLayerDown(cv);
            exportToJson();
            refreshLockedLayersPanel();
        });

// ── Send Back
        View vBack = cv.findViewById(R.id.btn_pop_send_back);
        if (vBack != null) vBack.setOnClickListener(v -> {
            sendViewToBack(cv);
            if (currentStickerToolbarPopup != null) currentStickerToolbarPopup.dismiss();
        });

// ── Move toggle
        if (btnToggleMove != null && rowMove != null) {
            btnToggleMove.setOnClickListener(v -> {
                boolean show = rowMove.getVisibility() != View.VISIBLE;
                rowMove.setVisibility(show ? View.VISIBLE : View.GONE);
                // બાકી hide
                if (show) {
                    rowLayer.setVisibility(View.GONE);
                    hsvAlign.setVisibility(View.GONE);
                    btnToggleLayer.setBackgroundColor(Color.parseColor("#4A148C"));
                    btnToggleAlign.setBackgroundColor(Color.parseColor("#1565C0"));
                }
                btnToggleMove.setBackgroundColor(show ? Color.parseColor("#546E7A") : Color.parseColor("#37474F"));
            });
        }

// ── Layer toggle
        if (btnToggleLayer != null && rowLayer != null) {
            btnToggleLayer.setOnClickListener(v -> {
                boolean show = rowLayer.getVisibility() != View.VISIBLE;
                rowLayer.setVisibility(show ? View.VISIBLE : View.GONE);
                if (show) {
                    rowMove.setVisibility(View.GONE);
                    hsvAlign.setVisibility(View.GONE);
                    btnToggleMove.setBackgroundColor(Color.parseColor("#37474F"));
                    btnToggleAlign.setBackgroundColor(Color.parseColor("#1565C0"));
                }
                btnToggleLayer.setBackgroundColor(show ? Color.parseColor("#6A1B9A") : Color.parseColor("#4A148C"));
            });
        }

// ── Align toggle
        if (btnToggleAlign != null && hsvAlign != null) {
            btnToggleAlign.setOnClickListener(v -> {
                boolean show = hsvAlign.getVisibility() != View.VISIBLE;
                hsvAlign.setVisibility(show ? View.VISIBLE : View.GONE);
                if (show) {
                    rowMove.setVisibility(View.GONE);
                    rowLayer.setVisibility(View.GONE);
                    btnToggleMove.setBackgroundColor(Color.parseColor("#37474F"));
                    btnToggleLayer.setBackgroundColor(Color.parseColor("#4A148C"));
                }
                btnToggleAlign.setBackgroundColor(show ? Color.parseColor("#1976D2") : Color.parseColor("#1565C0"));
            });
        }

        // ── Close button
        TextView btnSelClose = cv.findViewById(R.id.btn_sel_close);
        if (btnSelClose != null) {
            btnSelClose.setOnClickListener(v -> {
                try {
                    if (selectionControlsPopup != null && selectionControlsPopup.isShowing()) {
                        selectionControlsPopup.dismiss();
                    }
                } catch (Exception e) {
                    selectionControlsPopup = null;
                }
            });
        }

// ── Single view align — canvas based
        View.OnClickListener singleAlignListener = v -> {
            int id = v.getId();

            float canvasLeft = main_image_view.getX();
            float canvasTop = main_image_view.getY();
            float canvasRight = canvasLeft + main_image_view.getWidth();
            float canvasBottom = canvasTop + main_image_view.getHeight();
            float canvasCenterX = canvasLeft + main_image_view.getWidth() / 2f;
            float canvasCenterY = canvasTop + main_image_view.getHeight() / 2f;

            // ── Selected views list — single or multi
            List<View> targets = new ArrayList<>();
            if (!selectedViews.isEmpty()) {
                // Multi-select mode — selectedViews use
                targets.addAll(selectedViews);
            } else {
                // Single select — targetView use
                targets.add(targetView);
            }

            // ── 2+ views check for distribute
            if ((id == R.id.btn_sel_distribute_h || id == R.id.btn_sel_distribute_v) && targets.size() < 2) {
                Toast.makeText(MainActivity.this, "Distribute માટે 2+ elements select કરો", Toast.LENGTH_SHORT).show();
                return;
            }

            if (id == R.id.btn_sel_align_left) {
                for (View tv2 : targets)
                    tv2.setX(canvasLeft);

            } else if (id == R.id.btn_sel_align_center_h) {
                for (View tv2 : targets)
                    tv2.setX(canvasCenterX - tv2.getWidth() / 2f);

            } else if (id == R.id.btn_sel_align_right) {
                for (View tv2 : targets)
                    tv2.setX(canvasRight - tv2.getWidth());

            } else if (id == R.id.btn_sel_align_top) {
                for (View tv2 : targets)
                    tv2.setY(canvasTop);

            } else if (id == R.id.btn_sel_align_middle) {
                for (View tv2 : targets)
                    tv2.setY(canvasCenterY - tv2.getHeight() / 2f);

            } else if (id == R.id.btn_sel_align_bottom) {
                for (View tv2 : targets)
                    tv2.setY(canvasBottom - tv2.getHeight());

            } else if (id == R.id.btn_sel_distribute_h) {
                // X sort
                targets.sort((a, b) -> Float.compare(a.getX(), b.getX()));
                float totalW = 0;
                for (View tv2 : targets) totalW += tv2.getWidth();
                float gap = (main_image_view.getWidth() - totalW) / (targets.size() - 1);
                float curX = canvasLeft;
                for (View tv2 : targets) {
                    tv2.setX(curX);
                    curX += tv2.getWidth() + gap;
                }

            } else if (id == R.id.btn_sel_distribute_v) {
                // Y sort
                targets.sort((a, b) -> Float.compare(a.getY(), b.getY()));
                float totalH = 0;
                for (View tv2 : targets) totalH += tv2.getHeight();
                float gap = (main_image_view.getHeight() - totalH) / (targets.size() - 1);
                float curY = canvasTop;
                for (View tv2 : targets) {
                    tv2.setY(curY);
                    curY += tv2.getHeight() + gap;
                }
            }

            // groupStartPositions update
            groupStartPositions.clear();
            for (View sel : selectedViews) {
                groupStartPositions.add(new float[]{sel.getX(), sel.getY()});
            }

            exportToJson();
        };

// ── Bind all align buttons
        if (btnSelAlignLeft != null) btnSelAlignLeft.setOnClickListener(singleAlignListener);
        if (btnSelAlignCenterH != null) btnSelAlignCenterH.setOnClickListener(singleAlignListener);
        if (btnSelAlignRight != null) btnSelAlignRight.setOnClickListener(singleAlignListener);
        if (btnSelAlignTop != null) btnSelAlignTop.setOnClickListener(singleAlignListener);
        if (btnSelAlignMiddle != null) btnSelAlignMiddle.setOnClickListener(singleAlignListener);
        if (btnSelAlignBottom != null) btnSelAlignBottom.setOnClickListener(singleAlignListener);
        if (btnSelDistH != null) btnSelDistH.setOnClickListener(singleAlignListener);
        if (btnSelDistV != null) btnSelDistV.setOnClickListener(singleAlignListener);


        if (targetView instanceof StrokeTextView) {
            btnEdit.setVisibility(View.VISIBLE);
            btnEdit.setOnClickListener(v -> {
                selectionControlsPopup.dismiss();
                showEditTextDialog((TextView) targetView);
            });
        } else {
            if (btnEdit != null) btnEdit.setVisibility(View.GONE);
        }

        if (targetView instanceof TextView) {
            selOriginalSize = ((TextView) targetView).getTextSize();
        } else if (targetView instanceof ImageView && targetView != main_image_view) {
            android.view.ViewGroup.LayoutParams lp = targetView.getLayoutParams();
            selOriginalW = (lp != null && lp.width > 0) ? lp.width : targetView.getWidth();
            selOriginalH = (lp != null && lp.height > 0) ? lp.height : targetView.getHeight();
        }

        seekSize.setOnSeekBarChangeListener(new android.widget.SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(android.widget.SeekBar s, int progress, boolean fromUser) {
                if (!fromUser) return;
                applySelSizeChange(targetView, progress, tvLbl);
            }

            @Override
            public void onStartTrackingTouch(android.widget.SeekBar s) {
            }

            @Override
            public void onStopTrackingTouch(android.widget.SeekBar s) {
            }
        });

        btnSzM.setOnClickListener(v -> {
            int nv = Math.max(0, seekSize.getProgress() - 2);
            seekSize.setProgress(nv);
            applySelSizeChange(targetView, nv, tvLbl);
        });
        btnSzP.setOnClickListener(v -> {
            int nv = Math.min(100, seekSize.getProgress() + 2);
            seekSize.setProgress(nv);
            applySelSizeChange(targetView, nv, tvLbl);
        });
        btnSzM.setOnLongClickListener(v -> {
            int nv = Math.max(0, seekSize.getProgress() - 5);
            seekSize.setProgress(nv);
            applySelSizeChange(targetView, nv, tvLbl);
            return true;
        });
        btnSzP.setOnLongClickListener(v -> {
            int nv = Math.min(100, seekSize.getProgress() + 5);
            seekSize.setProgress(nv);
            applySelSizeChange(targetView, nv, tvLbl);
            return true;
        });

        btnUp.setOnClickListener(v -> moveSingleView(targetView, 0, -SEL_MOVE_STEP));
        btnDown.setOnClickListener(v -> moveSingleView(targetView, 0, SEL_MOVE_STEP));
        btnLeft.setOnClickListener(v -> moveSingleView(targetView, -SEL_MOVE_STEP, 0));
        btnRight.setOnClickListener(v -> moveSingleView(targetView, SEL_MOVE_STEP, 0));

        btnUp.setOnLongClickListener(v -> {
            moveSingleView(targetView, 0, -SEL_MOVE_STEP * 5);
            return true;
        });
        btnDown.setOnLongClickListener(v -> {
            moveSingleView(targetView, 0, SEL_MOVE_STEP * 5);
            return true;
        });
        btnLeft.setOnLongClickListener(v -> {
            moveSingleView(targetView, -SEL_MOVE_STEP * 5, 0);
            return true;
        });
        btnRight.setOnLongClickListener(v -> {
            moveSingleView(targetView, SEL_MOVE_STEP * 5, 0);
            return true;
        });


        // ✅ First time bottom, after move last moved position
        if (isSelControlsMoved) {
            selectionControlsPopup.showAtLocation(mainLayout, Gravity.TOP | Gravity.LEFT, selControlsLastX, selControlsLastY);
        } else {
            cv.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
            int popupW = cv.getMeasuredWidth();
            int popupH = cv.getMeasuredHeight();
            selControlsLastX = Math.max(0, (mainLayout.getWidth() - popupW) / 2);
            selControlsLastY = Math.max(0, mainLayout.getHeight() - popupH - 25);
            selectionControlsPopup.showAtLocation(mainLayout, Gravity.TOP | Gravity.LEFT, selControlsLastX, selControlsLastY);
        }
    }


    // Size apply helper
    private void applySelSizeChange(View targetView, int progress, TextView tvLbl) {
        float delta = (progress - 50) * 0.8f;
        tvLbl.setText((delta >= 0 ? "+" : "") + (int) delta);

        if (targetView instanceof TextView) {
            float density = getResources().getDisplayMetrics().scaledDensity;
            float origSp = selOriginalSize / density;
            float newSp = Math.max(8f, Math.min(120f, origSp + delta));
            ((TextView) targetView).setTextSize(newSp);

        } else if (targetView instanceof ImageView && targetView != main_image_view) {
            android.view.ViewGroup.LayoutParams lp = targetView.getLayoutParams();
            float dpDelta = (progress - 50) * 3f;
            lp.width = (int) Math.max(50, selOriginalW + dpDelta);
            lp.height = (int) Math.max(50, selOriginalH + dpDelta);
            targetView.setLayoutParams(lp);
        }
    }

    // Single view move
    private void moveSingleView(View v, float dx, float dy) {
        float newX = v.getX() + dx;
        float newY = v.getY() + dy;

        float minX = main_image_view.getLeft();
        float maxX = main_image_view.getRight() - v.getWidth();
        float minY = main_image_view.getTop();
        float maxY = main_image_view.getBottom() - v.getHeight();

        v.setX(Math.max(minX, Math.min(newX, maxX)));
        v.setY(Math.max(minY, Math.min(newY, maxY)));
    }

    private void addLocationIconWithLink(String mapUrl) {

        final ImageView locationImg = new ImageView(this);
        locationImg.setImageResource(R.drawable.location_9);

        // ✅ બે અલગ Tag વાપરો: એક ઓળખ માટે અને એક લિંક માટે
        locationImg.setTag(R.id.btn_set_background, "LOCATION_ICON");
        locationImg.setTag(R.id.btn_location, mapUrl);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(100, 100);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        locationImg.setLayoutParams(params);

        applyTouchListenerForSticker(locationImg); // Resize અને Move માટે

        locationImg.setOnClickListener(v -> {
            String url = (String) v.getTag(R.id.btn_location);
            if (url != null) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
            }
        });

        mainLayout.addView(locationImg);

    }

    private void applyTouchListenerForImage(final android.widget.ImageView imageView) {
        imageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // dX અને dY આપણે ઓલરેડી ગ્લોબલ જાહેર કરેલા છે
                        dX = view.getX() - event.getRawX();
                        dY = view.getY() - event.getRawY();
                        return true;

                    case MotionEvent.ACTION_MOVE:
                        float newX = event.getRawX() + dX;
                        float newY = event.getRawY() + dY;

                        // --- Boundary (ઈમેજની અંદર જ રહે તે માટે) ---
                        float minX = main_image_view.getLeft();
                        float maxX = main_image_view.getRight() - view.getWidth();
                        float minY = main_image_view.getTop();
                        float maxY = main_image_view.getBottom() - view.getHeight();

                        if (newX < minX) newX = minX;
                        if (newX > maxX) newX = maxX;
                        if (newY < minY) newY = minY;
                        if (newY > maxY) newY = maxY;

                        view.setX(newX);
                        view.setY(newY);
                        break;

                    case MotionEvent.ACTION_UP:
                        // જો ઈમેજ પર ક્લિક થાય તો લોકેશન લિંક એડિટ કરવાનો ઓપ્શન આપી શકાય
                        break;
                }
                return true;
            }
        });
    }


    private void saveClickablePdf() {
        String fileName = "Invitation_With_Link.pdf";
        File file = new File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), fileName);

        try {
            com.itextpdf.kernel.pdf.PdfWriter writer = new com.itextpdf.kernel.pdf.PdfWriter(new FileOutputStream(file));
            com.itextpdf.kernel.pdf.PdfDocument pdf = new com.itextpdf.kernel.pdf.PdfDocument(writer);

            // પેજની સાઈઝ mainLayout મુજબ સેટ કરો
            com.itextpdf.layout.Document document = new com.itextpdf.layout.Document(pdf, new com.itextpdf.kernel.geom.PageSize(mainLayout.getWidth(), mainLayout.getHeight()));
            document.setMargins(0, 0, 0, 0);

            for (int i = 0; i < mainLayout.getChildCount(); i++) {
                View v = mainLayout.getChildAt(i);

                if (v instanceof android.widget.ImageView) {
                    android.widget.ImageView iv = (android.widget.ImageView) v;

                    // Image ને Bitmap માં કન્વર્ટ કરો
                    iv.setDrawingCacheEnabled(true);
                    android.graphics.Bitmap bitmap = android.graphics.Bitmap.createBitmap(iv.getDrawingCache());
                    iv.setDrawingCacheEnabled(false);

                    // iText Image બનાવો
                    java.io.ByteArrayOutputStream stream = new java.io.ByteArrayOutputStream();
                    bitmap.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, stream);
                    com.itextpdf.layout.element.Image img = new com.itextpdf.layout.element.Image(com.itextpdf.io.image.ImageDataFactory.create(stream.toByteArray()));

                    // ઈમેજની પોઝિશન સેટ કરો
                    img.setFixedPosition(v.getX(), mainLayout.getHeight() - v.getY() - v.getHeight());
                    img.scaleToFit(v.getWidth(), v.getHeight());

                    // --- જો આ ઈમેજમાં મેપ લિંક હોય, તો લિંક સેટ કરો ---
                    String mapUrl = (String) v.getTag(R.id.btn_set_background);
                    if (mapUrl != null && !mapUrl.isEmpty()) {
                        img.setAction(com.itextpdf.kernel.pdf.action.PdfAction.createURI(mapUrl));
                    }

                    document.add(img);
                }
                // અહિયાં તમે TextViews પણ એડ કરી શકો છો...
            }

            document.close();
            Toast.makeText(this, "Clickable PDF saved in Downloads!", Toast.LENGTH_LONG).show();

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error creating PDF", Toast.LENGTH_SHORT).show();
        }
    }

    // ════════════════════════════════════════
    // SAVE ALL PAGES AS CLICKABLE PDF
    // ── Each page = main_image_view exact size
    // ════════════════════════════════════════
    private void saveAllPagesAsClickablePdf() {
        saveCurrentPage();

        String fileName = "MultiPage_Invitation_" + System.currentTimeMillis() + ".pdf";
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName);

        // ── PDF writer + document (no fixed page size)
        final com.itextpdf.kernel.pdf.PdfDocument[] pdfHolder = new com.itextpdf.kernel.pdf.PdfDocument[1];
        final com.itextpdf.layout.Document[] docHolder = new com.itextpdf.layout.Document[1];

        try {
            com.itextpdf.kernel.pdf.PdfWriter writer = new com.itextpdf.kernel.pdf.PdfWriter(new FileOutputStream(file));
            pdfHolder[0] = new com.itextpdf.kernel.pdf.PdfDocument(writer);

            // ✅ No fixed page size — each page adds its own
            docHolder[0] = new com.itextpdf.layout.Document(pdfHolder[0]);
            docHolder[0].setMargins(0, 0, 0, 0);

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "PDF init ભૂલ!", Toast.LENGTH_SHORT).show();
            return;
        }

        final android.os.Handler handler = new android.os.Handler();

        for (int i = 0; i < allPagesData.size(); i++) {
            final int index = i;
            final int pageNum = i + 1;

            handler.postDelayed(() -> {
                // Load page
                currentPageIndex = index;
                loadPageData(allPagesData.get(index));
                updatePageIndicator();

                // Wait for render
                mainLayout.postDelayed(() -> {
                    try {
                        // ── 1. Capture full layout bitmap
                        mainLayout.setDrawingCacheEnabled(true);
                        mainLayout.buildDrawingCache();
                        Bitmap fullBitmap = Bitmap.createBitmap(mainLayout.getDrawingCache());
                        mainLayout.setDrawingCacheEnabled(false);

                        // ── 2. Crop to main_image_view area
                        int imgX = (int) main_image_view.getX();
                        int imgY = (int) main_image_view.getY();
                        int imgW = main_image_view.getWidth();
                        int imgH = main_image_view.getHeight();

                        // ✅ Boundary check
                        imgX = Math.max(0, imgX);
                        imgY = Math.max(0, imgY);
                        if (imgX + imgW > fullBitmap.getWidth())
                            imgW = fullBitmap.getWidth() - imgX;
                        if (imgY + imgH > fullBitmap.getHeight())
                            imgH = fullBitmap.getHeight() - imgY;

                        Bitmap croppedBitmap = Bitmap.createBitmap(fullBitmap, imgX, imgY, imgW, imgH);

                        // ✅ Page size = cropped bitmap size exactly
                        float pageW = croppedBitmap.getWidth();
                        float pageH = croppedBitmap.getHeight();

                        Log.e("PDF", "Page " + pageNum + " size: " + pageW + "x" + pageH);

                        // ── 3. Add new PDF page with exact size
                        com.itextpdf.kernel.geom.PageSize thisPageSize = new com.itextpdf.kernel.geom.PageSize(pageW, pageH);
                        pdfHolder[0].addNewPage(thisPageSize);

                        // ── 4. Convert bitmap to PDF image
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        croppedBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);

                        com.itextpdf.layout.element.Image pdfImg = new com.itextpdf.layout.element.Image(com.itextpdf.io.image.ImageDataFactory.create(stream.toByteArray()));

                        // ✅ Fixed position — fill entire page
                        pdfImg.setFixedPosition(pageNum, // page number
                                0,       // x = left
                                0,       // y = bottom
                                pageW);  // width = full page

                        // ✅ Height also set
                        pdfImg.setHeight(pageH);

                        docHolder[0].add(pdfImg);

                        // ── 5. Location links
                        for (int j = 0; j < mainLayout.getChildCount(); j++) {
                            View v = mainLayout.getChildAt(j);
                            if (v == main_image_view) continue;

                            Object locTag = v.getTag(R.id.btn_location);
                            if (locTag == null) continue;

                            String mapUrl = locTag.toString();
                            if (mapUrl.isEmpty()) continue;

                            // PDF coordinates (bottom-left origin)
                            float lx = v.getX() - main_image_view.getX();
                            float ly = pageH - (v.getY() - main_image_view.getY()) - v.getHeight();

                            com.itextpdf.kernel.pdf.action.PdfAction action = com.itextpdf.kernel.pdf.action.PdfAction.createURI(mapUrl);

                            com.itextpdf.kernel.pdf.annot.PdfLinkAnnotation linkAnnotation = new com.itextpdf.kernel.pdf.annot.PdfLinkAnnotation(new com.itextpdf.kernel.geom.Rectangle(lx, ly, v.getWidth(), v.getHeight()));
                            linkAnnotation.setAction(action);
                            pdfHolder[0].getPage(pageNum).addAnnotation(linkAnnotation);
                        }

                        // ── 6. Last page = close + save
                        if (index == allPagesData.size() - 1) {
                            docHolder[0].close();

                            // Save to file manager
                            PdfFileManager.savePdf(MainActivity.this, fileName, file.getAbsolutePath());

                            // Media scan
                            android.media.MediaScannerConnection.scanFile(MainActivity.this, new String[]{file.getAbsolutePath()}, null, null);

                            Toast.makeText(MainActivity.this, "✅ PDF સેવ થઈ ગઈ!", Toast.LENGTH_LONG).show();

                            openPdfFile(file);
                        }

                        // Cleanup
                        croppedBitmap.recycle();
                        fullBitmap.recycle();
                        stream.close();

                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("PDF", "Page " + pageNum + " error: " + e.getMessage());
                    }

                }, 1200); // render wait

            }, i * 3000); // page gap
        }
    }


    private void openPdfFile(File file) {
        if (file == null || !file.exists()) {
            Toast.makeText(this, "PDF file મળ્યો નહીં", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(this, PdfViewerActivity.class);
        intent.putExtra("PDF_PATH", file.getAbsolutePath());
        startActivity(intent);
    }


    private void saveAllPagesAsPdf() {
        saveCurrentPage();
        Toast.makeText(this, "PDF બની રહી છે...", Toast.LENGTH_SHORT).show();

        android.graphics.pdf.PdfDocument pdfDocument = new android.graphics.pdf.PdfDocument();
        final android.os.Handler handler = new android.os.Handler();

        for (int i = 0; i < allPagesData.size(); i++) {
            final int index = i;

            handler.postDelayed(() -> {
                currentPageIndex = index;
                loadPageData(allPagesData.get(index));
                updatePageIndicator();

                mainLayout.postDelayed(() -> {
                    // ૧. સાઈઝ મેળવો
                    int width = main_image_view.getWidth();
                    int height = main_image_view.getHeight();

                    // ૨. પીડીએફ પેજ શરૂ કરો
                    android.graphics.pdf.PdfDocument.PageInfo pageInfo = new android.graphics.pdf.PdfDocument.PageInfo.Builder(width, height, index + 1).create();
                    android.graphics.pdf.PdfDocument.Page page = pdfDocument.startPage(pageInfo);
                    android.graphics.Canvas canvas = page.getCanvas();

                    // --- મુખ્ય સુધારો અહીં છે ---
                    // લેઆઉટનો આખો ફોટો (Bitmap) લો જેથી કંઈ પણ હાઈડ ન થાય
                    mainLayout.setDrawingCacheEnabled(true);
                    mainLayout.buildDrawingCache();
                    android.graphics.Bitmap bitmap = mainLayout.getDrawingCache();

                    if (bitmap != null) {
                        // માત્ર main_image_view ના ભાગને જ Bitmap માંથી કાપીને PDF માં દોરો
                        android.graphics.Rect src = new android.graphics.Rect(main_image_view.getLeft(), main_image_view.getTop(), main_image_view.getRight(), main_image_view.getBottom());
                        android.graphics.Rect dst = new android.graphics.Rect(0, 0, width, height);

                        canvas.drawBitmap(bitmap, src, dst, null);
                    }
                    mainLayout.setDrawingCacheEnabled(false);
                    // ---------------------------

                    pdfDocument.finishPage(page);

                    if (index == allPagesData.size() - 1) {
                        savePdfFile(pdfDocument);
                    }
                }, 800); // થોડો વધારે સમય આપો રેન્ડરિંગ માટે

            }, i * 2500);
        }
    }


    private void savePdfFile(android.graphics.pdf.PdfDocument pdfDocument) {
        String fileName = "Invitation_Card_" + System.currentTimeMillis() + ".pdf";

        // Downloads ફોલ્ડરમાં સેવ કરો
        File storageDir = new File(android.os.Environment.getExternalStoragePublicDirectory(android.os.Environment.DIRECTORY_DOWNLOADS), "MyCardMaker_PDF");

        if (!storageDir.exists()) storageDir.mkdirs();

        File pdfFile = new File(storageDir, fileName);

        try {
            pdfDocument.writeTo(new FileOutputStream(pdfFile));
            Toast.makeText(this, "PDF સેવ થઈ ગઈ: Downloads/MyCardMaker_PDF", Toast.LENGTH_LONG).show();

            // ગેલેરી/ફાઈલ મેનેજર સ્કેન
            android.media.MediaScannerConnection.scanFile(this, new String[]{pdfFile.getAbsolutePath()}, null, null);

        } catch (java.io.IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "PDF સેવ કરવામાં ભૂલ આવી!", Toast.LENGTH_SHORT).show();
        } finally {
            pdfDocument.close();
        }
    }

    private void saveLayoutAsImage() {
        // 1. લેઆઉટને Bitmap માં કન્વર્ટ કરો
        mainLayout.setDrawingCacheEnabled(true);
        mainLayout.buildDrawingCache();
        android.graphics.Bitmap bitmap = android.graphics.Bitmap.createBitmap(mainLayout.getDrawingCache());
        mainLayout.setDrawingCacheEnabled(false);

        // 2. ફાઈલનું નામ અને લોકેશન (Public Pictures Folder)
        String fileName = "Card_Page_" + (currentPageIndex + 1) + "_" + System.currentTimeMillis() + ".jpg";

        // ગેલેરીમાં દેખાય તે માટે Environment.DIRECTORY_PICTURES નો ઉપયોગ કરો
        File storageDir = new File(android.os.Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "MyCardMaker");

        if (!storageDir.exists()) {
            storageDir.mkdirs();
        }

        File imageFile = new File(storageDir, fileName);

        try {
            FileOutputStream fos = new FileOutputStream(imageFile);
            bitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();

            // --- સૌથી મહત્વનું: ગેલેરીમાં ઈમેજ બતાવવા માટે સ્કેન કરો ---
            android.media.MediaScannerConnection.scanFile(this, new String[]{imageFile.getAbsolutePath()}, new String[]{"image/jpeg"}, (path, uri) -> {
                // સ્કેન પૂરું થયા પછી લોગ અથવા ટોસ્ટ બતાવી શકાય
            });

            runOnUiThread(() -> Toast.makeText(this, "પેજ " + (currentPageIndex + 1) + " સેવ થયું!", Toast.LENGTH_SHORT).show());

        } catch (Exception e) {
            e.printStackTrace();
            runOnUiThread(() -> Toast.makeText(this, "સેવ કરવામાં ભૂલ આવી!", Toast.LENGTH_SHORT).show());
        }
    }


    // ════════════════════════════════════════
    // SHOW DELETED ITEMS DIALOG
    // Texts + Stickers both
    // ════════════════════════════════════════
    private void showDeletedTextsDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("🗑 Deleted Items");

        // ── Root layout
        android.widget.LinearLayout root = new android.widget.LinearLayout(this);
        root.setOrientation(android.widget.LinearLayout.VERTICAL);
        root.setBackgroundColor(Color.parseColor("#F5F5F5"));

        // ── Tab row
        android.widget.LinearLayout tabRow = new android.widget.LinearLayout(this);
        tabRow.setOrientation(android.widget.LinearLayout.HORIZONTAL);
        tabRow.setBackgroundColor(Color.WHITE);
        tabRow.setPadding(8, 8, 8, 0);

        Button btnTabText = new Button(this);
        btnTabText.setText("📝 Texts (" + deletedTextsList.size() + ")");
        btnTabText.setTextSize(12);
        btnTabText.setTextColor(Color.WHITE);
        btnTabText.setBackgroundColor(Color.parseColor("#1565C0"));
        android.widget.LinearLayout.LayoutParams tabP = new android.widget.LinearLayout.LayoutParams(0, android.widget.LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        tabP.setMargins(4, 0, 4, 0);
        btnTabText.setLayoutParams(tabP);

        Button btnTabSticker = new Button(this);
        btnTabSticker.setText("🖼 Stickers (" + deletedStickersList.size() + ")");
        btnTabSticker.setTextSize(12);
        btnTabSticker.setTextColor(Color.parseColor("#1565C0"));
        btnTabSticker.setBackgroundColor(Color.parseColor("#E3F2FD"));
        btnTabSticker.setLayoutParams(new android.widget.LinearLayout.LayoutParams(0, android.widget.LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

        tabRow.addView(btnTabText);
        tabRow.addView(btnTabSticker);
        root.addView(tabRow);

        // ── Content area
        android.widget.ScrollView scrollView = new android.widget.ScrollView(this);
        android.widget.LinearLayout.LayoutParams svP = new android.widget.LinearLayout.LayoutParams(android.widget.LinearLayout.LayoutParams.MATCH_PARENT, 600);
        scrollView.setLayoutParams(svP);

        // ── Text panel
        android.widget.LinearLayout panelText = new android.widget.LinearLayout(this);
        panelText.setOrientation(android.widget.LinearLayout.VERTICAL);
        panelText.setPadding(8, 8, 8, 8);

        // ── Sticker panel
        android.widget.LinearLayout panelSticker = new android.widget.LinearLayout(this);
        panelSticker.setOrientation(android.widget.LinearLayout.VERTICAL);
        panelSticker.setPadding(8, 8, 8, 8);
        panelSticker.setVisibility(View.GONE);

        scrollView.addView(panelText);
        root.addView(scrollView);

        // ── Bottom buttons
        android.widget.LinearLayout btnRow = new android.widget.LinearLayout(this);
        btnRow.setOrientation(android.widget.LinearLayout.HORIZONTAL);
        btnRow.setPadding(8, 8, 8, 8);
        btnRow.setBackgroundColor(Color.WHITE);

        Button btnDeleteAll = new Button(this);
        btnDeleteAll.setText("🗑 Delete All");
        btnDeleteAll.setTextColor(Color.WHITE);
        btnDeleteAll.setBackgroundColor(Color.parseColor("#C62828"));
        btnDeleteAll.setTextSize(12);
        android.widget.LinearLayout.LayoutParams daP = new android.widget.LinearLayout.LayoutParams(0, android.widget.LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        daP.setMargins(4, 0, 4, 0);
        btnDeleteAll.setLayoutParams(daP);

        Button btnClose = new Button(this);
        btnClose.setText("Close");
        btnClose.setTextSize(12);
        btnClose.setTextColor(Color.WHITE);
        btnClose.setBackgroundColor(Color.parseColor("#616161"));
        btnClose.setLayoutParams(new android.widget.LinearLayout.LayoutParams(0, android.widget.LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

        btnRow.addView(btnDeleteAll);
        btnRow.addView(btnClose);
        root.addView(btnRow);

        // ── Build dialog
        builder.setView(root);
        AlertDialog dialog = builder.create();

        // ── State
        final boolean[] isTextTab = {true};

        // ── Render tabs
        Runnable renderTextTab = () -> {
            panelText.removeAllViews();

            if (deletedTextsList.isEmpty()) {
                TextView empty = new TextView(this);
                empty.setText("કોઈ deleted text નથી");
                empty.setTextColor(Color.GRAY);
                empty.setPadding(16, 24, 16, 24);
                panelText.addView(empty);
                return;
            }

            for (int i = 0; i < deletedTextsList.size(); i++) {
                final int idx = i;
                JSONObject obj = deletedTextsList.get(i);
                String text = obj.optString("text", "(Empty)");

                // ── Row
                android.widget.LinearLayout row = new android.widget.LinearLayout(this);
                row.setOrientation(android.widget.LinearLayout.HORIZONTAL);
                row.setPadding(8, 8, 8, 8);
                row.setBackgroundColor(Color.WHITE);
                android.widget.LinearLayout.LayoutParams rP = new android.widget.LinearLayout.LayoutParams(android.widget.LinearLayout.LayoutParams.MATCH_PARENT, android.widget.LinearLayout.LayoutParams.WRAP_CONTENT);
                rP.setMargins(0, 0, 0, 2);
                row.setLayoutParams(rP);

                // Text
                TextView tv = new TextView(this);
                tv.setText(text);
                tv.setTextSize(14);
                tv.setTextColor(Color.parseColor("#212121"));
                android.widget.LinearLayout.LayoutParams tvP = new android.widget.LinearLayout.LayoutParams(0, android.widget.LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
                tv.setLayoutParams(tvP);
                row.addView(tv);

                // Restore
                Button btnRestore = new Button(this);
                btnRestore.setText("↩");
                btnRestore.setTextSize(13);
                btnRestore.setTextColor(Color.WHITE);
                btnRestore.setBackgroundColor(Color.parseColor("#1565C0"));
                btnRestore.setPadding(12, 4, 12, 4);
                android.widget.LinearLayout.LayoutParams brP = new android.widget.LinearLayout.LayoutParams(android.widget.LinearLayout.LayoutParams.WRAP_CONTENT, android.widget.LinearLayout.LayoutParams.WRAP_CONTENT);
                brP.setMargins(4, 0, 4, 0);
                btnRestore.setLayoutParams(brP);
                btnRestore.setOnClickListener(v -> {
                    try {
                        addNewTextViewFromLoad(deletedTextsList.get(idx));
                        deletedTextsList.remove(idx);
                        exportToJson();
                        dialog.dismiss();
                        showDeletedTextsDialog();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
                row.addView(btnRestore);

                // Permanent delete
                Button btnDel = new Button(this);
                btnDel.setText("🗑");
                btnDel.setTextSize(13);
                btnDel.setTextColor(Color.WHITE);
                btnDel.setBackgroundColor(Color.parseColor("#C62828"));
                btnDel.setPadding(12, 4, 12, 4);
                btnDel.setOnClickListener(v -> {
                    new AlertDialog.Builder(this).setTitle("Permanent Delete").setMessage("\"" + text + "\" permanently " + "delete કરવો?").setPositiveButton("Delete", (d2, w2) -> {
                        deletedTextsList.remove(idx);
                        exportToJson();
                        dialog.dismiss();
                        showDeletedTextsDialog();
                    }).setNegativeButton("Cancel", null).show();
                });
                row.addView(btnDel);

                panelText.addView(row);
            }
        };

        Runnable renderStickerTab = () -> {
            panelSticker.removeAllViews();

            if (deletedStickersList.isEmpty()) {
                TextView empty = new TextView(this);
                empty.setText("કોઈ deleted sticker નથી");
                empty.setTextColor(Color.GRAY);
                empty.setPadding(16, 24, 16, 24);
                panelSticker.addView(empty);
                return;
            }

            for (int i = 0; i < deletedStickersList.size(); i++) {
                final int idx = i;
                JSONObject obj = deletedStickersList.get(i);
                String uri = obj.optString("uri", "");

                // ── Row
                android.widget.LinearLayout row = new android.widget.LinearLayout(this);
                row.setOrientation(android.widget.LinearLayout.HORIZONTAL);
                row.setPadding(8, 8, 8, 8);
                row.setBackgroundColor(Color.WHITE);
                android.widget.LinearLayout.LayoutParams rP = new android.widget.LinearLayout.LayoutParams(android.widget.LinearLayout.LayoutParams.MATCH_PARENT, android.widget.LinearLayout.LayoutParams.WRAP_CONTENT);
                rP.setMargins(0, 0, 0, 2);
                row.setLayoutParams(rP);

                // ── Thumb
                ImageView thumb = new ImageView(this);
                int size = 80;
                android.widget.LinearLayout.LayoutParams imgP = new android.widget.LinearLayout.LayoutParams(size, size);
                imgP.setMargins(0, 0, 8, 0);
                thumb.setLayoutParams(imgP);
                thumb.setScaleType(ImageView.ScaleType.CENTER_CROP);
                thumb.setBackgroundColor(Color.parseColor("#EEEEEE"));

                if (uri.startsWith("http")) {
                    Glide.with(this).load(uri).into(thumb);
                } else if (uri.startsWith("file://")) {
                    Glide.with(this).load(new java.io.File(uri.replace("file://", ""))).into(thumb);
                } else if ("FRAMED_IMAGE".equals(uri)) {
                    thumb.setImageResource(android.R.drawable.ic_menu_gallery);
                } else if ("LOCATION_ICON".equals(uri)) {
                    thumb.setImageResource(R.drawable.location_9);
                } else {
                    thumb.setImageResource(android.R.drawable.ic_menu_gallery);
                }
                row.addView(thumb);

                // Label
                String label;
                if ("FRAMED_IMAGE".equals(uri)) label = "🖼 Frame";
                else if ("LOCATION_ICON".equals(uri)) label = "📍 Location";
                else if (uri.startsWith("http"))
                    label = "🌐 " + uri.substring(uri.lastIndexOf('/') + 1);
                else label = "📁 " + uri.substring(uri.lastIndexOf('/') + 1);

                TextView tv = new TextView(this);
                tv.setText(label);
                tv.setTextSize(12);
                tv.setTextColor(Color.parseColor("#212121"));
                tv.setEllipsize(android.text.TextUtils.TruncateAt.END);
                tv.setMaxLines(2);
                android.widget.LinearLayout.LayoutParams tvP = new android.widget.LinearLayout.LayoutParams(0, android.widget.LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
                tv.setLayoutParams(tvP);
                row.addView(tv);

                // ── Restore
                Button btnRestore = new Button(this);
                btnRestore.setText("↩");
                btnRestore.setTextSize(13);
                btnRestore.setTextColor(Color.WHITE);
                btnRestore.setBackgroundColor(Color.parseColor("#1565C0"));
                btnRestore.setPadding(12, 4, 12, 4);
                android.widget.LinearLayout.LayoutParams brP = new android.widget.LinearLayout.LayoutParams(android.widget.LinearLayout.LayoutParams.WRAP_CONTENT, android.widget.LinearLayout.LayoutParams.WRAP_CONTENT);
                brP.setMargins(4, 0, 4, 0);
                btnRestore.setLayoutParams(brP);
                btnRestore.setOnClickListener(v -> {
                    try {
                        // ── Restore sticker
                        addStickerFromLoad(deletedStickersList.get(idx));
                        deletedStickersList.remove(idx);
                        exportToJson();
                        dialog.dismiss();
                        showDeletedTextsDialog();
                        Toast.makeText(this, "✅ Sticker restore થઈ ગયો!", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
                row.addView(btnRestore);

                // ── Permanent delete
                Button btnDel = new Button(this);
                btnDel.setText("🗑");
                btnDel.setTextSize(13);
                btnDel.setTextColor(Color.WHITE);
                btnDel.setBackgroundColor(Color.parseColor("#C62828"));
                btnDel.setPadding(12, 4, 12, 4);
                btnDel.setOnClickListener(v -> {
                    new AlertDialog.Builder(this).setTitle("Permanent Delete").setMessage("આ sticker permanently" + " delete કરવો?").setPositiveButton("Delete", (d2, w2) -> {
                        deletedStickersList.remove(idx);
                        exportToJson();
                        dialog.dismiss();
                        showDeletedTextsDialog();
                    }).setNegativeButton("Cancel", null).show();
                });
                row.addView(btnDel);

                panelSticker.addView(row);
            }
        };

        // ── Tab switch
        btnTabText.setOnClickListener(v -> {
            isTextTab[0] = true;
            btnTabText.setBackgroundColor(Color.parseColor("#1565C0"));
            btnTabText.setTextColor(Color.WHITE);
            btnTabSticker.setBackgroundColor(Color.parseColor("#E3F2FD"));
            btnTabSticker.setTextColor(Color.parseColor("#1565C0"));

            scrollView.removeAllViews();
            scrollView.addView(panelText);
            renderTextTab.run();
        });

        btnTabSticker.setOnClickListener(v -> {
            isTextTab[0] = false;
            btnTabSticker.setBackgroundColor(Color.parseColor("#1565C0"));
            btnTabSticker.setTextColor(Color.WHITE);
            btnTabText.setBackgroundColor(Color.parseColor("#E3F2FD"));
            btnTabText.setTextColor(Color.parseColor("#1565C0"));

            scrollView.removeAllViews();
            scrollView.addView(panelSticker);
            renderStickerTab.run();
        });

        // ── Delete All
        btnDeleteAll.setOnClickListener(v -> {
            String target = isTextTab[0] ? "texts" : "stickers";
            int count = isTextTab[0] ? deletedTextsList.size() : deletedStickersList.size();

            if (count == 0) {
                Toast.makeText(this, "Delete list ખાલી છે", Toast.LENGTH_SHORT).show();
                return;
            }

            new AlertDialog.Builder(this).setTitle("⚠ Delete All " + target).setMessage(count + " " + target + " permanently delete કરવા?").setPositiveButton("Delete All", (d2, w2) -> {
                if (isTextTab[0]) deletedTextsList.clear();
                else deletedStickersList.clear();
                exportToJson();
                dialog.dismiss();
                showDeletedTextsDialog();
            }).setNegativeButton("Cancel", null).show();
        });

        // ── Close
        btnClose.setOnClickListener(v -> dialog.dismiss());

        // ── Initial render
        renderTextTab.run();

        dialog.show();
    }


    private void setDeletedTextAdapter(RecyclerView recyclerView, AlertDialog dialog, Button btnDeleteAll) {

        // ── Update Delete All button visibility
        if (btnDeleteAll != null) {
            btnDeleteAll.setVisibility(deletedTextsList.isEmpty() ? View.GONE : View.VISIBLE);
        }

        DeletedTextAdapter adapter = new DeletedTextAdapter(deletedTextsList, new DeletedTextAdapter.OnActionListener() {

            // ── Restore
            @Override
            public void onRestore(int position) {
                try {
                    if (position < 0 || position >= deletedTextsList.size()) return;

                    JSONObject deletedObj = deletedTextsList.get(position);
                    addNewTextViewFromLoad(deletedObj);
                    deletedTextsList.remove(position);
                    exportToJson();

                    Toast.makeText(MainActivity.this, "✅ Text Restore થઈ ગયો", Toast.LENGTH_SHORT).show();

                    // Refresh
                    setDeletedTextAdapter(recyclerView, dialog, btnDeleteAll);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            // ── Permanent delete single
            @Override
            public void onPermanentDelete(int position) {
                if (position < 0 || position >= deletedTextsList.size()) return;

                String text = deletedTextsList.get(position).optString("text", "Text");

                new AlertDialog.Builder(MainActivity.this).setTitle("Permanent Delete").setMessage("\"" + text + "\" permanently " + "delete કરવો?\n" + "Restore નહીં થઈ શકે!").setPositiveButton("Delete", (d, w) -> {
                    deletedTextsList.remove(position);
                    exportToJson();

                    Toast.makeText(MainActivity.this, "🗑 Permanently deleted", Toast.LENGTH_SHORT).show();

                    // Refresh
                    setDeletedTextAdapter(recyclerView, dialog, btnDeleteAll);

                    // Close if empty
                    if (deletedTextsList.isEmpty()) {
                        dialog.dismiss();
                    }
                }).setNegativeButton("Cancel", null).show();
            }
        });

        recyclerView.setAdapter(adapter);
    }

    private void saveCurrentPage() {
        JSONObject page = getCurrentPageAsJson();

        if (allPagesData == null) {
            allPagesData = new ArrayList<>();
        }

        if (allPagesData.size() == 0) {
            allPagesData.add(page);
            currentPageIndex = 0;
        } else if (currentPageIndex >= 0 && currentPageIndex < allPagesData.size()) {
            allPagesData.set(currentPageIndex, page);
        } else {
            allPagesData.add(page);
            currentPageIndex = allPagesData.size() - 1;
        }
    }

    private JSONObject createEmptyPage() {
        JSONObject obj = new JSONObject();
        try {
            obj.put("imageUrl", "");  // ← empty = no background
            obj.put("texts", new JSONArray());
            obj.put("stickers", new JSONArray());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return obj;
    }

    private void setBackgroundFromUrl(String imageUrl) {
        currentImageUrl = imageUrl;

        // ✅ Actual image size load → main_image_view resize
        Glide.with(this).asBitmap().load(imageUrl).into(new CustomTarget<Bitmap>() {
            @Override
            public void onResourceReady(@NonNull Bitmap bitmap, @Nullable Transition<? super Bitmap> t) {

                // ── Actual image dimensions
                int imgW = bitmap.getWidth();
                int imgH = bitmap.getHeight();

                // ── main_image_view container size
                int containerW = mainLayout.getWidth();
                int containerH = mainLayout.getHeight();

                // ── Scale to fit container maintaining ratio
                float scaleW = (float) containerW / imgW;
                float scaleH = (float) containerH / imgH;
                float scale = Math.min(scaleW, scaleH);

                int finalW = (int) (imgW * scale);
                int finalH = (int) (imgH * scale);

                // ✅ Resize main_image_view to exact ratio
                ViewGroup.LayoutParams lp = main_image_view.getLayoutParams();
                lp.width = finalW;
                lp.height = finalH;
                main_image_view.setLayoutParams(lp);

                // ✅ FIT_XY — no letterbox
                main_image_view.setScaleType(ImageView.ScaleType.FIT_XY);
                main_image_view.setImageBitmap(bitmap);

                Log.e("BG", "Image: " + imgW + "x" + imgH + " → View: " + finalW + "x" + finalH);
            }

            @Override
            public void onLoadCleared(@Nullable Drawable placeholder) {
            }
        });
    }

    private void showDeletedPagesDialog() {
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_deleted_pages, null);

        final PopupWindow movablePopup = new PopupWindow(dialogView, 600, 800, true);
        movablePopup.setElevation(20);

        LinearLayout listContainer = dialogView.findViewById(R.id.deleted_pages_container);
        listContainer.removeAllViews();

        if (deletedPagesList.isEmpty()) {
            TextView tv = new TextView(this);
            tv.setText("કોઈ ડીલીટ પેજ નથી");
            listContainer.addView(tv);
        }

        for (int i = 0; i < deletedPagesList.size(); i++) {
            final int index = i;
            Button btnPage = new Button(this);
            btnPage.setText("Recover Page " + (i + 1));
            btnPage.setOnClickListener(v -> {
                allPagesData.add(deletedPagesList.get(index));
                deletedPagesList.remove(index);
                movablePopup.dismiss();
                updatePageIndicator();
                exportToJson();
                Toast.makeText(this, "Page Recovered!", Toast.LENGTH_SHORT).show();
            });
            listContainer.addView(btnPage);
        }

        dialogView.findViewById(R.id.dialog_header).setOnTouchListener(new View.OnTouchListener() {
            private float initialX, initialY, initialTouchX, initialTouchY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        return true;

                    case MotionEvent.ACTION_MOVE:
                        float dx = event.getRawX() - initialTouchX;
                        float dy = event.getRawY() - initialTouchY;
                        movablePopup.update((int) (initialX + dx), (int) (initialY + dy), -1, -1);
                        return true;
                }
                return false;
            }
        });

        movablePopup.showAtLocation(mainLayout, Gravity.CENTER, 0, 0);
    }

    private void exportToJson() {
        try {
            if (allPagesData.size() > 0 && currentPageIndex >= 0 && currentPageIndex < allPagesData.size()) {
                allPagesData.set(currentPageIndex, getCurrentPageAsJson());
            }

            JSONObject finalObject = new JSONObject();

            JSONArray mainPagesArray = new JSONArray();
            for (JSONObject page : allPagesData) {
                mainPagesArray.put(page);
            }
            finalObject.put("main_pages", mainPagesArray);

            JSONArray deletedPagesArray = new JSONArray();
            for (JSONObject deletedPage : deletedPagesList) {
                deletedPagesArray.put(deletedPage);
            }
            finalObject.put("deleted_pages", deletedPagesArray);

            JSONArray deletedTextsArray = new JSONArray();
            for (JSONObject deletedText : deletedTextsList) {
                deletedTextsArray.put(deletedText);
            }
            finalObject.put("deleted_texts", deletedTextsArray);

            JSONArray deletedStickersArray = new JSONArray();
            for (JSONObject s : deletedStickersList) {
                deletedStickersArray.put(s);
            }
            finalObject.put("deleted_stickers", deletedStickersArray);

            saveToFile(finalObject.toString());
            Toast.makeText(this, "ડિઝાઇન સેવ થઈ ગઈ!", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveToFile(String jsonData) {
        File file;
        if (currentOpenFilePath != null) {
            file = new File(currentOpenFilePath);
        } else {
            String fileName = "design_" + System.currentTimeMillis() + ".json";
            file = new File(getExternalFilesDir(null), fileName);
            currentOpenFilePath = file.getAbsolutePath();
        }

        Log.println(ASSERT, "currentOpenFilePath", currentOpenFilePath + "");
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(jsonData.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void importFromJson(String filePath) {
        try {
            File file = new File(filePath);
            if (!file.exists()) return;

            FileInputStream fis = new FileInputStream(file);
            byte[] data = new byte[(int) file.length()];
            fis.read(data);
            fis.close();

            String jsonString = new String(data, "UTF-8");
            JSONObject mainObj = new JSONObject(jsonString);

            allPagesData.clear();
            deletedPagesList.clear();
            deletedTextsList.clear();

            JSONArray mainArray = mainObj.optJSONArray("main_pages");
            if (mainArray != null) {
                for (int i = 0; i < mainArray.length(); i++) {
                    allPagesData.add(mainArray.getJSONObject(i));
                }
            }

            JSONArray delPagesArray = mainObj.optJSONArray("deleted_pages");
            if (delPagesArray != null) {
                for (int i = 0; i < delPagesArray.length(); i++) {
                    deletedPagesList.add(delPagesArray.getJSONObject(i));
                }
            }

            JSONArray delTextsArray = mainObj.optJSONArray("deleted_texts");
            if (delTextsArray != null) {
                for (int i = 0; i < delTextsArray.length(); i++) {
                    deletedTextsList.add(delTextsArray.getJSONObject(i));
                }
            }

            // deleted_texts load ની નીચે
            JSONArray delStickersArray = mainObj.optJSONArray("deleted_stickers");
            if (delStickersArray != null) {
                for (int i = 0; i < delStickersArray.length(); i++) {
                    deletedStickersList.add(delStickersArray.getJSONObject(i));
                }
            }


            if (allPagesData.isEmpty()) {
                allPagesData.add(createEmptyPage());
            }

            currentPageIndex = 0;
            loadPageData(allPagesData.get(currentPageIndex));
            updatePageIndicator();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private JSONObject getCurrentPageAsJson() {
        JSONObject pageObject = new JSONObject();
        JSONArray textArray = new JSONArray();
        JSONArray stickerArray = new JSONArray();
        JSONArray gridArray = new JSONArray();

        try {
            float sw = mainLayout.getWidth();
            float sh = mainLayout.getHeight();

            if (sw <= 0) return createEmptyPage();

            pageObject.put("imageUrl",
                    currentImageUrl != null ? currentImageUrl : "");

            for (int i = 0; i < mainLayout.getChildCount(); i++) {
                View v = mainLayout.getChildAt(i);
                if (v == main_image_view) continue;

                // xPercent/yPercent calculate કરતી વખતે main_image_view offset remove
                float offsetX = main_image_view.getX();
                float offsetY = main_image_view.getY();

                Object bgTag = v.getTag(R.id.btn_set_background);
                String tagStr = bgTag != null ? bgTag.toString() : "";

                // ✅ GRID FRAME — separate save
                if ("GRID_FRAME".equals(tagStr)) {
                    try {
                        RelativeLayout gridContainer = (RelativeLayout) v;
                        JSONObject gridObj = new JSONObject();

                        gridObj.put("xPercent", (v.getX() / sw) * 100);
                        gridObj.put("yPercent", (v.getY() / sh) * 100);
                        gridObj.put("width", v.getLayoutParams().width);
                        gridObj.put("height", v.getLayoutParams().height);
                        gridObj.put("rotation", v.getRotation());

                        GridMeta meta = null;
                        Object metaTag = v.getTag(R.id.btn_grid_frame);
                        if (metaTag instanceof GridMeta) {
                            meta = (GridMeta) metaTag;
                        }

                        int rows = meta != null ? meta.rows : 3;
                        int cols = meta != null ? meta.cols : 4;
                        String shape = meta != null ? meta.shape : "ROUNDED";

                        gridObj.put("gridTag", rows + "x" + cols + "_" + shape);
                        gridObj.put("rows", rows);
                        gridObj.put("cols", cols);
                        gridObj.put("shape", shape);
                        gridObj.put("cellSizePx", meta != null ? meta.cellSizePx : 200);
                        gridObj.put("showName", meta == null || meta.showName);
                        gridObj.put("showInfo", meta == null || meta.showInfo);

                        JSONArray cellsArray = new JSONArray();

                        int totalCells = rows * cols;

                        for (int ci = 0; ci < totalCells; ci++) {
                            JSONObject cellObj = new JSONObject();

                            // ✅ First save from GridMeta data list
                            if (meta != null && meta.cellDataList != null && ci < meta.cellDataList.size()) {
                                JSONObject old = meta.cellDataList.get(ci);

                                cellObj.put("photoUri", old.optString("photoUri", ""));
                                cellObj.put("name", old.optString("name", "Name " + (ci + 1)));
                                cellObj.put("info", old.optString("info", "0.00%"));

                                if (old.has("photoBitmap")) {
                                    cellObj.put("photoBitmap", old.optString("photoBitmap", ""));
                                }
                            } else {
                                cellObj.put("photoUri", "");
                                cellObj.put("name", "Name " + (ci + 1));
                                cellObj.put("info", "0.00%");
                            }

                            // ✅ Also save current ImageView bitmap as Base64
                            if (ci < gridContainer.getChildCount()) {
                                View cellView = gridContainer.getChildAt(ci);

                                if (cellView instanceof LinearLayout) {
                                    LinearLayout cellLL = (LinearLayout) cellView;

                                    for (int k = 0; k < cellLL.getChildCount(); k++) {
                                        View child = cellLL.getChildAt(k);

                                        if (child instanceof ImageView) {
                                            ImageView iv = (ImageView) child;

                                            if (iv.getDrawable() != null) {
                                                try {
                                                    Bitmap bmp = null;

                                                    if (iv.getDrawable() instanceof android.graphics.drawable.BitmapDrawable) {
                                                        bmp = ((android.graphics.drawable.BitmapDrawable) iv.getDrawable()).getBitmap();
                                                    }

                                                    if (bmp != null) {
                                                        Bitmap resized = resizeBitmapForStorage(bmp);
                                                        cellObj.put("photoBitmap", bitmapToBase64(resized));

                                                        if (resized != bmp) {
                                                            resized.recycle();
                                                        }
                                                    }
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }

                                        if (child instanceof TextView) {
                                            Object msTag = child.getTag(R.id.btn_ms_select_all);

                                            if (msTag != null && msTag.toString().startsWith("NAME_")) {
                                                cellObj.put("name", ((TextView) child).getText().toString());
                                            }

                                            if (msTag != null && msTag.toString().startsWith("INFO_")) {
                                                cellObj.put("info", ((TextView) child).getText().toString());
                                            }
                                        }
                                    }
                                }
                            }

                            cellsArray.put(cellObj);
                        }


                        gridObj.put("cells", cellsArray);
                        gridArray.put(gridObj);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    continue;
                }

                // ✅ TEXT save
                if (v instanceof StrokeTextView) {
                    StrokeTextView tv = (StrokeTextView) v;
                    try {
                        JSONObject obj = new JSONObject();
                        obj.put("text", tv.getText().toString());
// આ કરો:
                        float imgLeft = main_image_view.getLeft();
                        float imgTop = main_image_view.getTop();
                        float imgW = main_image_view.getWidth();
                        float imgH = main_image_view.getHeight();

                        obj.put("xPercent", ((v.getX() + v.getWidth() / 2f - imgLeft) / imgW) * 100f);
                        obj.put("yPercent", ((v.getY() + v.getHeight() / 2f - imgTop) / imgH) * 100f);


                        obj.put("anchor", "CENTER");

                        obj.put("color",
                                tv.getCurrentTextColor());
                        obj.put("sizePercent",
                                (tv.getTextSize() / sw) * 100);
                        obj.put("rotation", tv.getRotation());
                      /*  obj.put("bgColor",
                                getStoredBackgroundColor(tv));*/
                        obj.put("isBold",
                                tv.getTypeface() != null
                                        && tv.getTypeface().isBold());
                        obj.put("isUnderline",
                                (tv.getPaintFlags()
                                        & Paint.UNDERLINE_TEXT_FLAG) != 0);
                        obj.put("strokeWidth",
                                tv.getStrokeWidth());
                        obj.put("strokeColor",
                                tv.getStrokeColor());
                        obj.put("layerIndex",
                                mainLayout.indexOfChild(v));
                        obj.put("isLocked",
                                lockedViews.contains(v));
                        obj.put("isArcMode", tv.isArcMode());

                        obj.put("textGravity", tv.getGravity());


                        Object borderTag = tv.getTag(R.id.btn_add_sticker);
                        int borderStyleVal = borderTag instanceof Integer ? (int) borderTag : 0;
                        obj.put("borderStyle", borderStyleVal);

                        // ── Full border info save (width, color, corner)
                        Object borderInfoTag = tv.getTag(R.id.tv_border_info);
                        if (borderInfoTag instanceof int[]) {
                            int[] binfo = (int[]) borderInfoTag;
                            obj.put("borderWidth",  binfo.length > 0 ? binfo[0] : 0);
                            obj.put("borderColor",  binfo.length > 1 ? binfo[1] : Color.TRANSPARENT);
                            obj.put("borderCorner", binfo.length > 2 ? binfo[2] : 0);
                        } else {
                            obj.put("borderWidth",  0);
                            obj.put("borderColor",  Color.TRANSPARENT);
                            obj.put("borderCorner", 0);
                        }

                        // ── Border Color (background color)
                        int savedBgColor = getStoredBackgroundColor(tv);
                        obj.put("bgColor", savedBgColor);

                        // ── Padding — user value tag માંથી
                        Object padTag = tv.getTag(R.id.btn_location);
                        if (padTag instanceof int[]) {
                            int[] userPad = (int[]) padTag;
                            obj.put("paddingLeft", userPad[0]);
                            obj.put("paddingTop", userPad[1]);
                            obj.put("paddingRight", userPad[0]);
                            obj.put("paddingBottom", userPad[1]);
                        } else {
                            // fallback
                            int se = (int) Math.ceil(tv.getStrokeWidth()) + 4;
                            int pl = Math.max(0, tv.getPaddingLeft() - se);
                            int pt = Math.max(0, tv.getPaddingTop() - se);
                            obj.put("paddingLeft", pl);
                            obj.put("paddingTop", pt);
                            obj.put("paddingRight", pl);
                            obj.put("paddingBottom", pt);
                            // ✅ tag પણ set કરો future save માટે
                            tv.setTag(R.id.btn_location, new int[]{pl, pt});
                        }


                        obj.put("lineSpacingMult", tv.getLineSpacingMultiplier());
                        obj.put("letterSpacing", tv.getLetterSpacing());
                        obj.put("alpha", tv.getAlpha());
                        obj.put("scaleX", tv.getScaleX());
                        obj.put("scaleY", tv.getScaleY());
                        obj.put("shadowRadius", tv.getShadowRadius());
                        obj.put("shadowDx", tv.getShadowDx());
                        obj.put("shadowDy", tv.getShadowDy());
                        obj.put("shadowColor", tv.getShadowColor());

                        if (tv.isArcMode()) {
                            obj.put("arcAngle",
                                    tv.getArcAngle());
                            obj.put("arcRadius",
                                    tv.getRadius());
                            obj.put("arcUp", tv.isArcUp());
                        }

                        Object bgImgTag =
                                tv.getTag(R.id.btn_sticker_gallery);
                        if (bgImgTag != null
                                && !bgImgTag.toString().isEmpty()) {
                            obj.put("bgImageUri",
                                    bgImgTag.toString());
                        }

                        // Gradient save
                        Object gradTag = tv.getTag(R.id.tv_move_speed);
                        if (gradTag != null && !gradTag.toString().isEmpty()) {
                            obj.put("gradientColors", gradTag.toString());
                        }


                        textArray.put(obj);
                    } catch (Exception te) {
                        te.printStackTrace();
                    }
                    continue;
                }

                // ✅ STICKER / IMAGE save
                if (v instanceof ImageView && v != main_image_view) {
                    ImageView iv = (ImageView) v;
                    try {
                        JSONObject sObj = new JSONObject();
                        String uriStr = tagStr;
                        sObj.put("uri", uriStr);

                        if ("FRAMED_IMAGE".equals(uriStr)) {
                            Object topTag =
                                    iv.getTag(R.id.btn_add_sticker);
                            Object maskTag =
                                    iv.getTag(R.id.btn_location);
                            Object colorTag =
                                    iv.getTag(R.id.seek_multi_size);
                            Object bmpTag =
                                    iv.getTag(R.id.tv_size_label);

                            sObj.put("isFramedImage", true);
                            sObj.put("frameTopUrl",
                                    topTag != null
                                            ? topTag.toString() : "");
                            sObj.put("frameMaskUrl",
                                    maskTag != null
                                            ? maskTag.toString() : "");
                            sObj.put("frameColor",
                                    colorTag instanceof Integer
                                            ? (int) colorTag
                                            : Color.TRANSPARENT);

                            if (bmpTag instanceof Bitmap) {
                                Bitmap bmp = (Bitmap) bmpTag;
                                Bitmap resized =
                                        resizeBitmapForStorage(bmp);
                                String b64 =
                                        bitmapToBase64(resized);
                                if (!b64.isEmpty()) {
                                    sObj.put("userMaskedBase64",
                                            b64);
                                }
                                if (resized != bmp)
                                    resized.recycle();
                            }
                        } else {
                            sObj.put("isFramedImage", false);
                            Object locTag =
                                    iv.getTag(R.id.btn_location);
                            if (locTag != null)
                                sObj.put("mapUrl",
                                        locTag.toString());
                            Object catTag =
                                    iv.getTag(R.id.btn_add_sticker);
                            sObj.put("catid",
                                    catTag != null
                                            ? catTag.toString() : "");
                        }


                        float imgLeft = main_image_view.getLeft();
                        float imgTop = main_image_view.getTop();
                        float imgW = main_image_view.getWidth();
                        float imgH = main_image_view.getHeight();

                        sObj.put("xPercent", ((iv.getX() - imgLeft) / imgW) * 100);
                        sObj.put("yPercent", ((iv.getY() - imgTop) / imgH) * 100);

                        sObj.put("rotation", iv.getRotation());
                        sObj.put("isLocked",
                                lockedViews.contains(v));
                        sObj.put("scaleX", iv.getScaleX());
                        sObj.put("scaleY", iv.getScaleY());

                        ViewGroup.LayoutParams lp =
                                iv.getLayoutParams();
                        int ww = (lp != null && lp.width > 0)
                                ? lp.width : iv.getWidth();
                        int hh = (lp != null && lp.height > 0)
                                ? lp.height : iv.getHeight();
                        sObj.put("widthPercent",
                                ((float) ww / sw) * 100);
                        sObj.put("heightPercent",
                                ((float) hh / sh) * 100);

                        stickerArray.put(sObj);
                    } catch (Exception se) {
                        se.printStackTrace();
                    }
                }
            }

            pageObject.put("texts", textArray);
            pageObject.put("stickers", stickerArray);
            pageObject.put("grids", gridArray);

            // ── Groups save
            JSONArray groupsArray = new JSONArray();
            for (int g = 0; g < allGroups.size(); g++) {
                List<View> group = allGroups.get(g);
                JSONArray groupViewsArray = new JSONArray();
                float sw2 = mainLayout.getWidth();
                float sh2 = mainLayout.getHeight();

                for (View gv : group) {
                    if (gv.getParent() != mainLayout) continue;
                    try {
                        JSONObject vObj = new JSONObject();
                        vObj.put("xPercent",
                                (gv.getX() / sw2) * 100);
                        vObj.put("yPercent",
                                (gv.getY() / sh2) * 100);
                        vObj.put("rotation", gv.getRotation());
                        vObj.put("scaleX", gv.getScaleX());
                        vObj.put("scaleY", gv.getScaleY());

                        if (gv instanceof StrokeTextView) {
                            vObj.put("type", "text");
                            StrokeTextView tv =
                                    (StrokeTextView) gv;
                            vObj.put("text",
                                    tv.getText().toString());
                            vObj.put("color",
                                    tv.getCurrentTextColor());
                            vObj.put("sizePercent",
                                    (tv.getTextSize() / sw2) * 100);
                        } else if (gv instanceof ImageView) {
                            vObj.put("type", "sticker");
                            Object uriTag =
                                    gv.getTag(R.id.btn_set_background);
                            vObj.put("uri",
                                    uriTag != null
                                            ? uriTag.toString() : "");
                            ViewGroup.LayoutParams lp =
                                    gv.getLayoutParams();
                            int gw = lp != null && lp.width > 0
                                    ? lp.width : gv.getWidth();
                            int gh = lp != null && lp.height > 0
                                    ? lp.height : gv.getHeight();
                            vObj.put("widthPercent",
                                    ((float) gw / sw2) * 100);
                            vObj.put("heightPercent",
                                    ((float) gh / sh2) * 100);
                        }
                        groupViewsArray.put(vObj);
                    } catch (Exception ge) {
                        ge.printStackTrace();
                    }
                }
                if (groupViewsArray.length() > 0) {
                    groupsArray.put(groupViewsArray);
                }
            }
            pageObject.put("groups", groupsArray);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return pageObject;
    }


    // ── Bitmap → Base64
    private String bitmapToBase64(Bitmap bitmap) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                bitmap.compress(Bitmap.CompressFormat.WEBP_LOSSY, 70, baos);
            } else {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos);
            }
            return android.util.Base64.encodeToString(baos.toByteArray(), android.util.Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    // ── Base64 → Bitmap
    private Bitmap base64ToBitmap(String base64Str) {
        try {
            byte[] decodedBytes = android.util.Base64.decode(
                    base64Str,
                    android.util.Base64.DEFAULT
            );

            return BitmapFactory.decodeByteArray(
                    decodedBytes,
                    0,
                    decodedBytes.length
            );

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // ── Storage resize — max 500px
    private Bitmap resizeBitmapForStorage(Bitmap src) {
        int maxSize = 500;
        int w = src.getWidth();
        int h = src.getHeight();
        if (w <= maxSize && h <= maxSize) return src;
        float ratio = Math.min((float) maxSize / w, (float) maxSize / h);
        return Bitmap.createScaledBitmap(src, (int) (w * ratio), (int) (h * ratio), true);
    }

    private void addStickerFromLoad(JSONObject obj) throws JSONException {

        final ImageView sticker = new ImageView(this);

        String uriString = obj.optString("uri", "");
        boolean isFramedImage = obj.optBoolean("isFramedImage", false);

        // ── Lock state
        boolean isStickerLocked = obj.optBoolean("isLocked", false);
        if (isStickerLocked) {
            mainLayout.post(() -> {
                lockedViews.add(sticker);
                updateLockIcon(sticker);
                sticker.setOnTouchListener((v, event) -> {
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        showLockPopup(sticker);
                    }
                    return true;
                });
            });
        }

        if (isFramedImage && "FRAMED_IMAGE".equals(uriString)) {

            // ── Framed image
            String topUrl = obj.optString("frameTopUrl", "");
            String maskUrl = obj.optString("frameMaskUrl", "");
            int color = obj.optInt("frameColor", Color.TRANSPARENT);
            String base64 = obj.optString("userMaskedBase64", "");

            Bitmap userMaskedBitmap = base64.isEmpty() ? null : base64ToBitmap(base64);

            sticker.setTag(R.id.btn_set_background, "FRAMED_IMAGE");
            sticker.setTag(R.id.btn_add_sticker, topUrl);
            sticker.setTag(R.id.btn_location, maskUrl);
            sticker.setTag(R.id.seek_multi_size, color);
            if (userMaskedBitmap != null) {
                sticker.setTag(R.id.tv_size_label, userMaskedBitmap);
            }

            if (!topUrl.isEmpty()) {
                final Bitmap finalUserMasked = userMaskedBitmap;
                final int finalColor = color;

                Glide.with(this).asBitmap().load(topUrl).into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap topBitmap, @Nullable Transition<? super Bitmap> t) {

                        int w = topBitmap.getWidth();
                        int h = topBitmap.getHeight();

                        Bitmap result = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
                        Canvas canvas = new Canvas(result);
                        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
                        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);

                        // Layer 1: userMasked
                        if (finalUserMasked != null) {
                            Bitmap us = Bitmap.createScaledBitmap(finalUserMasked, w, h, true);
                            canvas.drawBitmap(us, 0, 0, paint);
                            us.recycle();
                        }

                        // Layer 2: top + color
                        if (finalColor != Color.TRANSPARENT) {
                            Bitmap tinted = applyColorTint(topBitmap, finalColor);
                            canvas.drawBitmap(tinted, 0, 0, paint);
                            tinted.recycle();
                        } else {
                            canvas.drawBitmap(topBitmap, 0, 0, paint);
                        }

                        sticker.setImageBitmap(result);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable p) {
                    }

                    @Override
                    public void onLoadFailed(@Nullable Drawable e) {
                        sticker.setImageResource(android.R.drawable.ic_menu_gallery);
                    }
                });
            } else {
                sticker.setImageResource(android.R.drawable.ic_menu_gallery);
            }

            applyTouchListenerForSticker(sticker);
            mainLayout.addView(sticker);

        } else {
            // ── Normal sticker
            String mapUrl = obj.optString("mapUrl", "");
            String catId = obj.optString("catid", "");

            if (!uriString.isEmpty() && !uriString.equals("LOCATION_ICON")) {

                // ✅ Load image — URL or local
                if (uriString.startsWith("http")) {
                    // ✅ Online server URL
                    Glide.with(this).load(uriString).placeholder(android.R.drawable.ic_menu_gallery).into(sticker);

                } else if (uriString.startsWith("file://")) {
                    // Local file
                    java.io.File f = new java.io.File(uriString.replace("file://", ""));
                    if (f.exists()) {
                        Glide.with(this).load(f).into(sticker);
                    } else {
                        sticker.setImageResource(android.R.drawable.ic_menu_gallery);
                    }

                } else if (uriString.startsWith("RES_")) {
                    // Resource ID
                    try {
                        int resId = Integer.parseInt(uriString.replace("RES_", ""));
                        sticker.setImageResource(resId);
                    } catch (Exception ignore) {
                    }

                } else {
                    // content:// or other
                    sticker.setImageURI(Uri.parse(uriString));
                }

                sticker.setTag(R.id.btn_set_background, uriString);
                sticker.setTag(R.id.btn_add_sticker, catId);

            } else {
                // ── Location icon
                sticker.setImageResource(R.drawable.location_9);
                sticker.setTag(R.id.btn_set_background, "LOCATION_ICON");
                if (!mapUrl.isEmpty()) sticker.setTag(R.id.btn_location, mapUrl);
            }

            applyTouchListenerForSticker(sticker);

            if (!mapUrl.isEmpty()) {
                sticker.setOnClickListener(v -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(mapUrl))));
            }

            mainLayout.addView(sticker);
        }

        // ── Position + Size
        mainLayout.post(() -> {
            try {
                float rotation = (float) obj.optDouble("rotation", 0f);
                sticker.setRotation(rotation);

                float sw = mainLayout.getWidth();
                float sh = mainLayout.getHeight();
                float offsetX = main_image_view.getX();
                float offsetY = main_image_view.getY();

                // આ કરો:
                float imgLeft = main_image_view.getLeft();
                float imgTop = main_image_view.getTop();
                float imgW = main_image_view.getWidth();
                float imgH = main_image_view.getHeight();

                sticker.setX(imgLeft + (float) (obj.getDouble("xPercent") * imgW / 100));
                sticker.setY(imgTop + (float) (obj.getDouble("yPercent") * imgH / 100));

                int finalW, finalH;
                if (obj.has("widthPercent")) {
                    finalW = (int) (obj.getDouble("widthPercent") * sw / 100);
                    finalH = (int) (obj.getDouble("heightPercent") * sh / 100);
                } else {
                    finalW = obj.optInt("width", 300);
                    finalH = obj.optInt("height", 300);
                }

                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(finalW, finalH);
                sticker.setLayoutParams(params);

                float scaleX = (float) obj.optDouble("scaleX", 1.0f);
                float scaleY = (float) obj.optDouble("scaleY", 1.0f);
                sticker.setScaleX(scaleX);
                sticker.setScaleY(scaleY);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
    }

    private void loadGridsFromJson(JSONArray gridsArray) {
        if (gridsArray == null || gridsArray.length() == 0) return;

        float sw = mainLayout.getWidth();
        float sh = mainLayout.getHeight();

        if (sw <= 0 || sh <= 0) {
            mainLayout.postDelayed(() -> loadGridsFromJson(gridsArray), 300);
            return;
        }

        for (int g = 0; g < gridsArray.length(); g++) {
            try {
                JSONObject gridObj = gridsArray.getJSONObject(g);

                String gridTag = gridObj.optString("gridTag", "3x4_ROUNDED");
                android.util.Log.d("GRID_LOAD", "Loading grid: " + gridTag);

                String shape = "ROUNDED";
                int rows = 3;
                int cols = 4;

                try {
                    String[] parts = gridTag.split("_");

                    if (parts.length >= 2) {
                        shape = parts[1];
                    }

                    String[] dims = parts[0].split("x");
                    if (dims.length >= 2) {
                        rows = Integer.parseInt(dims[0].trim());
                        cols = Integer.parseInt(dims[1].trim());
                    }

                } catch (Exception parseEx) {
                    android.util.Log.e("GRID_LOAD", "Tag parse error: " + parseEx.getMessage());
                }

                int w = gridObj.optInt("width", 0);
                int h = gridObj.optInt("height", 0);

                int gap = 8;

                if (w <= 0 || h <= 0) {
                    int defaultCellSize = 200;
                    w = cols * defaultCellSize + (cols - 1) * gap;
                    h = rows * (defaultCellSize + 40 + 30) + (rows - 1) * gap;
                }

                float x = (float) (gridObj.optDouble("xPercent", 0) * sw / 100f);
                float y = (float) (gridObj.optDouble("yPercent", 0) * sh / 100f);
                float rotation = (float) gridObj.optDouble("rotation", 0);

                JSONArray cells = gridObj.optJSONArray("cells");

                if (cells == null || cells.length() == 0) {
                    android.util.Log.w("GRID_LOAD", "No cells found!");
                    continue;
                }

                boolean hasName = false;
                boolean hasInfo = false;

                for (int i = 0; i < cells.length(); i++) {
                    JSONObject checkObj = cells.optJSONObject(i);
                    if (checkObj == null) continue;

                    if (checkObj.has("name")) {
                        hasName = true;
                    }

                    if (checkObj.has("info")) {
                        hasInfo = true;
                    }
                }

                if (!hasName) hasName = true;
                if (!hasInfo) hasInfo = true;

                int nameH = hasName ? 40 : 0;
                int infoH = hasInfo ? 30 : 0;

                int finalCellW = Math.max(60, (w - ((cols - 1) * gap)) / cols);
                int cellTotalH = finalCellW + nameH + infoH;

                int totalW = cols * finalCellW + (cols - 1) * gap;
                int totalH = rows * cellTotalH + (rows - 1) * gap;

                final String finalShape = shape;
                final int finalCellSize = finalCellW;
                final boolean finalHasName = hasName;
                final boolean finalHasInfo = hasInfo;

                RelativeLayout gridContainer = new RelativeLayout(this);

                RelativeLayout.LayoutParams outerLp =
                        new RelativeLayout.LayoutParams(totalW, totalH);

                gridContainer.setLayoutParams(outerLp);

                ArrayList<JSONObject> loadedCellDataList = new ArrayList<>();

                int totalCells = rows * cols;

                for (int i = 0; i < totalCells; i++) {
                    JSONObject cellObj;

                    if (i < cells.length()) {
                        cellObj = cells.optJSONObject(i);
                        if (cellObj == null) {
                            cellObj = new JSONObject();
                        }
                    } else {
                        cellObj = new JSONObject();
                    }

                    if (!cellObj.has("photoUri")) {
                        cellObj.put("photoUri", "");
                    }

                    if (!cellObj.has("name")) {
                        cellObj.put("name", "Name " + (i + 1));
                    }

                    if (!cellObj.has("info")) {
                        cellObj.put("info", "0.00%");
                    }

                    loadedCellDataList.add(cellObj);
                }

                for (int r = 0; r < rows; r++) {
                    for (int c = 0; c < cols; c++) {

                        final int cellIdx = r * cols + c;

                        if (cellIdx >= loadedCellDataList.size()) continue;

                        final JSONObject cellObj = loadedCellDataList.get(cellIdx);

                        LinearLayout cell = new LinearLayout(this);
                        cell.setOrientation(LinearLayout.VERTICAL);
                        cell.setGravity(Gravity.CENTER_HORIZONTAL);

                        RelativeLayout.LayoutParams cellLp =
                                new RelativeLayout.LayoutParams(finalCellSize, cellTotalH);

                        cellLp.leftMargin = c * (finalCellSize + gap);
                        cellLp.topMargin = r * (cellTotalH + gap);

                        cell.setLayoutParams(cellLp);

                        ImageView photoIv = new ImageView(this);

                        LinearLayout.LayoutParams photoLp =
                                new LinearLayout.LayoutParams(finalCellSize, finalCellSize);

                        photoIv.setLayoutParams(photoLp);
                        photoIv.setScaleType(ImageView.ScaleType.CENTER_CROP);

                        GradientDrawable photoGd = new GradientDrawable();
                        photoGd.setColor(Color.parseColor("#DDDDDD"));

                        if ("CIRCLE".equals(finalShape)) {
                            photoGd.setShape(GradientDrawable.OVAL);
                        } else if ("ROUNDED".equals(finalShape)) {
                            photoGd.setCornerRadius(finalCellSize * 0.12f);
                        }

                        photoIv.setBackground(photoGd);
                        photoIv.setClipToOutline(true);

                        if ("CIRCLE".equals(finalShape)) {
                            photoIv.setOutlineProvider(new ViewOutlineProvider() {
                                @Override
                                public void getOutline(View v, android.graphics.Outline outline) {
                                    outline.setOval(0, 0, v.getWidth(), v.getHeight());
                                }
                            });
                        } else if ("ROUNDED".equals(finalShape)) {
                            final float rad = finalCellSize * 0.12f;

                            photoIv.setOutlineProvider(new ViewOutlineProvider() {
                                @Override
                                public void getOutline(View v, android.graphics.Outline outline) {
                                    outline.setRoundRect(
                                            0,
                                            0,
                                            v.getWidth(),
                                            v.getHeight(),
                                            rad
                                    );
                                }
                            });
                        }

                        photoIv.setTag(R.id.btn_set_background, "GRID_CELL_" + cellIdx);

                        String photoBitmapBase64 = cellObj.optString("photoBitmap", "");
                        String photoUri = cellObj.optString("photoUri", "");

                        if (!photoBitmapBase64.isEmpty()) {
                            try {
                                Bitmap bmp = base64ToBitmap(photoBitmapBase64);

                                if (bmp != null) {
                                    Bitmap clipped = clipBitmapToShape(bmp, finalCellSize, finalShape);
                                    photoIv.setImageBitmap(clipped);
                                    photoIv.setBackground(null);
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        } else if (!photoUri.isEmpty()) {
                            Glide.with(this)
                                    .asBitmap()
                                    .load(Uri.parse(photoUri))
                                    .into(new CustomTarget<Bitmap>(finalCellSize, finalCellSize) {
                                        @Override
                                        public void onResourceReady(
                                                @NonNull Bitmap bitmap,
                                                @Nullable Transition<? super Bitmap> transition) {

                                            Bitmap clipped = clipBitmapToShape(
                                                    bitmap,
                                                    finalCellSize,
                                                    finalShape
                                            );

                                            photoIv.setImageBitmap(clipped);
                                            photoIv.setBackground(null);
                                        }

                                        @Override
                                        public void onLoadCleared(@Nullable Drawable placeholder) {
                                        }
                                    });
                        }

                        final ImageView finalPhoto = photoIv;

                        setGridCellPhotoTouch(
                                finalPhoto,
                                gridContainer,
                                cellIdx,
                                loadedCellDataList,
                                finalShape,
                                finalCellSize
                        );

                        cell.addView(photoIv);

                        TextView nameTv = null;
                        TextView infoTv = null;

                        if (finalHasName) {
                            nameTv = new TextView(this);
                            nameTv.setText(cellObj.optString("name", "Name " + (cellIdx + 1)));
                            nameTv.setTextSize(13);
                            // loadedCellDataList build કરતી વખતે, name TextView ને:
// nameTv.setTextColor(Color.BLACK);  ← આ line બદલો

                            int nc = Color.BLACK;
                            try {
                                Object ncObj = cellObj.get("nameColor");
                                if (ncObj instanceof Integer) nc = (Integer) ncObj;
                                else if (ncObj instanceof Long) nc = ((Long) ncObj).intValue();
                                else if (ncObj instanceof String) {
                                    String s = ((String) ncObj).trim();
                                    if (!s.isEmpty()) nc = Integer.parseInt(s);
                                }
                            } catch (Exception ignore) {
                            }
                            nameTv.setTextColor(nc);
                            nameTv.setGravity(Gravity.CENTER);
                            nameTv.setTypeface(null, Typeface.BOLD);
                            nameTv.setMaxLines(1);
                            nameTv.setEllipsize(android.text.TextUtils.TruncateAt.END);
                            nameTv.setTag(R.id.btn_ms_select_all, "NAME_" + cellIdx);

                            LinearLayout.LayoutParams nameLp =
                                    new LinearLayout.LayoutParams(
                                            LinearLayout.LayoutParams.MATCH_PARENT,
                                            nameH
                                    );

                            nameTv.setLayoutParams(nameLp);

                            cell.addView(nameTv);
                        }

                        if (finalHasInfo) {
                            infoTv = new TextView(this);
                            infoTv.setText(cellObj.optString("info", "0.00%"));
                            infoTv.setTextSize(12);
                            infoTv.setTextColor(Color.parseColor("#FF9800"));
                            infoTv.setGravity(Gravity.CENTER);
                            infoTv.setTypeface(null, Typeface.BOLD);
                            infoTv.setMaxLines(1);
                            infoTv.setTag(R.id.btn_ms_select_all, "INFO_" + cellIdx);

                            LinearLayout.LayoutParams infoLp =
                                    new LinearLayout.LayoutParams(
                                            LinearLayout.LayoutParams.MATCH_PARENT,
                                            infoH
                                    );

                            infoTv.setLayoutParams(infoLp);

                            cell.addView(infoTv);
                        }

                        final TextView finalName = nameTv;
                        final TextView finalInfo = infoTv;

                        if (finalName != null) {
                            finalName.setOnClickListener(v ->
                                    showCellEditDialog(
                                            finalPhoto,
                                            finalName,
                                            null,
                                            cellIdx,
                                            loadedCellDataList
                                    )
                            );
                        }

                        if (finalInfo != null) {
                            finalInfo.setOnClickListener(v ->
                                    showCellEditDialog(
                                            finalPhoto,
                                            null,
                                            finalInfo,
                                            cellIdx,
                                            loadedCellDataList
                                    )
                            );
                        }

                        gridContainer.addView(cell);
                    }
                }

                gridContainer.setTag(R.id.btn_set_background, "GRID_FRAME");

                gridContainer.setTag(R.id.btn_grid_frame, new GridMeta(
                        rows,
                        cols,
                        finalShape,
                        finalCellSize,
                        finalHasName,
                        finalHasInfo,
                        loadedCellDataList
                ));

                applyTouchListenerForGrid(gridContainer);
                attachGridEditOpenListener(gridContainer);

                mainLayout.addView(gridContainer);

                gridContainer.setX(x);
                gridContainer.setY(y);
                gridContainer.setRotation(rotation);

                android.util.Log.d(
                        "GRID_LOAD",
                        "Grid loaded with edit popup: " + rows + "x" + cols
                );

            } catch (Exception gridEx) {
                android.util.Log.e(
                        "GRID_LOAD",
                        "Grid " + g + " error: " + gridEx.getMessage()
                );
                gridEx.printStackTrace();
            }
        }
    }


    private void loadPageData(JSONObject pageObject) {

        scaleFactor = 1.0f;

        if (selectionControlsPopup != null &&
                selectionControlsPopup.isShowing()) {
            selectionControlsPopup.dismiss();
        }

        // ── Clean existing views
        for (int i = mainLayout.getChildCount() - 1; i >= 0; i--) {
            View v = mainLayout.getChildAt(i);
            if (v == main_image_view) continue;

            // ✅ Grid container remove
            Object tag = v.getTag(R.id.btn_set_background);
            if ("GRID_FRAME".equals(tag)) {
                mainLayout.removeViewAt(i);
                continue;
            }

            if (v instanceof TextView ||
                    (v instanceof ImageView &&
                            v != main_image_view)) {
                mainLayout.removeViewAt(i);
            }
        }

        try {
            String imageUrl = pageObject.optString("imageUrl", "");
            currentImageUrl = imageUrl;

            // ✅ Class level variables declare
            final JSONArray textArray =
                    pageObject.optJSONArray("texts");
            final JSONArray stickerArray =
                    pageObject.optJSONArray("stickers");
            final JSONArray gridsArr =
                    pageObject.optJSONArray("grids");
            final JSONArray groupsArr =
                    pageObject.optJSONArray("groups");

            if (!imageUrl.isEmpty()) {
                Glide.with(this)
                        .asBitmap()
                        .load(imageUrl)
                        .into(new com.bumptech.glide.request.target.CustomTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(
                                    @NonNull Bitmap bitmap,
                                    @Nullable com.bumptech.glide.request.transition.Transition<? super Bitmap> transition) {

                                int imgW = bitmap.getWidth();
                                int imgH = bitmap.getHeight();
                                int containerW = mainLayout.getWidth();
                                int containerH = mainLayout.getHeight();

                                if (containerW <= 0 || containerH <= 0) {
                                    main_image_view.setScaleType(
                                            ImageView.ScaleType.FIT_CENTER);
                                    main_image_view.setImageBitmap(bitmap);
                                    loadTextsAndStickers(textArray, stickerArray);
                                    return;
                                }

                                float scaleW = (float) containerW / imgW;
                                float scaleH = (float) containerH / imgH;
                                float scale = Math.min(scaleW, scaleH);

                                int finalW = (int) (imgW * scale);
                                int finalH = (int) (imgH * scale);

                                ViewGroup.LayoutParams lp =
                                        main_image_view.getLayoutParams();
                                lp.width = finalW;
                                lp.height = finalH;
                                main_image_view.setLayoutParams(lp);

                                main_image_view.setScaleType(
                                        ImageView.ScaleType.FIT_XY);
                                main_image_view.setImageBitmap(bitmap);

                                main_image_view.post(() -> {
                                    loadTextsAndStickers(textArray, stickerArray);

                                    if (gridsArr != null &&
                                            gridsArr.length() > 0) {
                                        mainLayout.postDelayed(() ->
                                                loadGridsFromJson(gridsArr), 300);
                                    }

                                    if (groupsArr != null &&
                                            groupsArr.length() > 0) {
                                        mainLayout.postDelayed(() ->
                                                loadGroupsFromJson(groupsArr), 500);
                                    }
                                });
                            }

                            @Override
                            public void onLoadCleared(
                                    @Nullable Drawable placeholder) {
                            }

                            @Override
                            public void onLoadFailed(
                                    @Nullable Drawable errorDrawable) {
                                Glide.with(MainActivity.this)
                                        .load(imageUrl)
                                        .into(main_image_view);
                                loadTextsAndStickers(textArray, stickerArray);
                            }
                        });
            } else {
                // ── Empty page
                ViewGroup.LayoutParams lp =
                        main_image_view.getLayoutParams();
                lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
                lp.height = ViewGroup.LayoutParams.MATCH_PARENT;
                main_image_view.setLayoutParams(lp);
                main_image_view.setScaleType(
                        ImageView.ScaleType.FIT_CENTER);
                main_image_view.setImageDrawable(null);
                main_image_view.setBackgroundColor(Color.WHITE);

                // ✅ Empty page — texts + stickers + grids
                loadTextsAndStickers(textArray, stickerArray);

                if (gridsArr != null && gridsArr.length() > 0) {
                    mainLayout.postDelayed(() ->
                            loadGridsFromJson(gridsArr), 300);
                }

                if (groupsArr != null && groupsArr.length() > 0) {
                    mainLayout.postDelayed(() ->
                            loadGroupsFromJson(groupsArr), 500);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // ── Helper: load texts + stickers
    private void loadTextsAndStickers(JSONArray textArray, JSONArray stickerArray) {
        try {
            if (textArray != null) {
                for (int i = 0; i < textArray.length(); i++) {
                    addNewTextViewFromLoad(textArray.getJSONObject(i));
                }
            }
            if (stickerArray != null) {
                for (int i = 0; i < stickerArray.length(); i++) {
                    addStickerFromLoad(stickerArray.getJSONObject(i));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void removeBackgroundOffline(Uri imageUri) {
        Toast.makeText(this, "Background remove thay chhe, raah juo...", Toast.LENGTH_SHORT).show();

        new Thread(() -> {
            try {
                // 1. URI mathi image bytes lo
                InputStream inputStream = getContentResolver().openInputStream(imageUri);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte[] buf = new byte[4096];
                int len;
                while ((len = inputStream.read(buf)) != -1) baos.write(buf, 0, len);
                byte[] imageBytes = baos.toByteArray();
                inputStream.close();

                // 2. Poof.bg API request banavo
                RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM).addFormDataPart("image_file", "image.png", RequestBody.create(imageBytes, MediaType.parse("image/png"))).addFormDataPart("size", "full").build();

                Request request = new Request.Builder().url("https://api.poof.bg/v1/remove").header("x-api-key", "pk_9f5b2c7cd9f23ede87977f498808f72b").post(requestBody).build();

                // 3. API call karo
                httpClient.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        runOnUiThread(() -> {
                            Log.println(ASSERT, "network error", e.getMessage());
                            Toast.makeText(MainActivity.this, "Network error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        });
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (response.isSuccessful() && response.body() != null) {
                            // Poof.bg direct PNG return kare chhe
                            byte[] resultBytes = response.body().bytes();
                            Bitmap resultBitmap = BitmapFactory.decodeByteArray(resultBytes, 0, resultBytes.length);

                            runOnUiThread(() -> {
                                if (resultBitmap != null) {
                                    addBitmapAsSticker(resultBitmap);
                                    Toast.makeText(MainActivity.this, "✅ Background remove thai gayu!", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            String errorMsg = response.body() != null ? response.body().string() : "Unknown error";
                            runOnUiThread(() -> {
                                Toast.makeText(MainActivity.this, "API Error " + response.code() + ": " + errorMsg, Toast.LENGTH_LONG).show();
                                Log.println(ASSERT, "network error", errorMsg);
                            });
                        }
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(MainActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }
        }).start();
    }

    private void addBitmapAsSticker(android.graphics.Bitmap bitmap) {
        final ImageView sticker = new ImageView(this);
        sticker.setImageBitmap(bitmap);

        // આને સેવ કરવા માટે કોઈ યુનિક આઈડી કે ટેગ આપો
        sticker.setTag(R.id.btn_set_background, "OFFLINE_REMOVED_BG");

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(500, 500);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        sticker.setLayoutParams(params);

        applyTouchListenerForSticker(sticker);
        mainLayout.addView(sticker);
        selectView(sticker);
    }


    private void updatePageIndicator() {
        txtPageIndicator.setText("Page: " + (currentPageIndex + 1) + " / " + allPagesData.size());
    }

/*    private void addNewTextViewFromLoad(JSONObject obj) throws JSONException {


        final StrokeTextView textView = new StrokeTextView(this);
        textView.setText(obj.getString("text"));
        textView.setTextColor(obj.getInt("color"));



        boolean bold = obj.optBoolean("isBold", false);
        boolean underline = obj.optBoolean("isUnderline", false);
        applyStyleToTextView(textView, bold, underline);

        // ── Stroke load
        float strokeWidth = (float) obj.optDouble("strokeWidth", 0f);
        int strokeColor = obj.optInt("strokeColor", Color.BLACK);
        textView.setStrokeWidth(strokeWidth);
        textView.setStrokeColor(strokeColor);

        *//*int bgColor = obj.optInt("bgColor", Color.TRANSPARENT);*//*

// ── Gradient load — stroke set પછી
        String gradColors = obj.optString("gradientColors", "");
        if (!gradColors.isEmpty()) {
            String[] parts = gradColors.split(",");
            if (parts.length >= 3) {
                try {
                    int gc1 = Integer.parseInt(parts[0].trim());
                    int gc2 = Integer.parseInt(parts[1].trim());
                    String gdir = parts[2].trim();

                    // ✅ Tag store
                    textView.setTag(R.id.tv_move_speed,
                            gc1 + "," + gc2 + "," + gdir);

                    // ✅ Layout ready પછી gradient apply
                    textView.post(() -> {
                        applyGradientToView(textView, gc1, gc2, gdir);


                        // ✅ Gradient apply પછી stroke re-set — shader conflict avoid
                        textView.post(() -> {
                            textView.setStrokeWidth(strokeWidth);
                            textView.setStrokeColor(strokeColor);
                            textView.invalidate();
                        });
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }



        String bgImageUri = obj.optString("bgImageUri", "");
        if (!bgImageUri.isEmpty()) {
            final StrokeTextView finalTextView = textView;
            Uri bgUri = Uri.parse(bgImageUri);
            // post — view add + measure પછી load
            mainLayout.post(() -> applyTextBgImage(bgUri, finalTextView));
        }


        boolean isLocked = obj.optBoolean("isLocked", false);
        if (isLocked) {
            // post — view add થઈ ગયા પછી lock apply
            final StrokeTextView finalView = textView;
            mainLayout.post(() -> {
                lockedViews.add(finalView);
                updateLockIcon(finalView);
                finalView.setOnTouchListener((v, event) -> {
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        showLockPopup(finalView);
                    }
                    return true;
                });
            });
        }



        int gravity = obj.optInt("textGravity", android.view.Gravity.CENTER);
        textView.setGravity(gravity);



        int borderStyle = obj.optInt("borderStyle", 0);
        int bgColor     = obj.optInt("bgColor", Color.TRANSPARENT);

        // ── Full border info load karo
        int borderWidth  = obj.optInt("borderWidth",  0);
        int borderColor  = obj.optInt("borderColor",  Color.TRANSPARENT);
        int borderCorner = obj.optInt("borderCorner", 0);

        GradientDrawable gd = new GradientDrawable();
        gd.setColor(bgColor);
        if (borderWidth > 0) {
            gd.setCornerRadius(dpToPx(borderCorner));
            switch (borderStyle) {
                case 0: gd.setStroke(dpToPx(borderWidth), borderColor); break;
                case 1: gd.setStroke(dpToPx(borderWidth), borderColor, dpToPx(8), dpToPx(4)); break;
                case 2: gd.setStroke(dpToPx(borderWidth), borderColor, dpToPx(2), dpToPx(4)); break;
                case 3: gd.setStroke(dpToPx(borderWidth + 2), borderColor); break;
                default: applyBorderStyle(gd, borderStyle);
            }
            // tv_border_info tag set karo jethi restore thay
            textView.setTag(R.id.tv_border_info, new int[]{borderWidth, borderColor, borderCorner, borderStyle});
        } else {
            applyBorderStyle(gd, borderStyle);
        }
        textView.setBackground(gd);
        textView.setTag(bgColor);                          // bg color tag
        textView.setTag(R.id.btn_add_sticker, borderStyle); // border style tag

// ── Padding load
        int savedPadLeft   = obj.optInt("paddingLeft",   20);
        int savedPadTop    = obj.optInt("paddingTop",     20);
        int savedPadRight  = obj.optInt("paddingRight",   savedPadLeft);
        int savedPadBottom = obj.optInt("paddingBottom",  savedPadTop);


       // int strokeExtra    = (int) Math.ceil(strokeWidth) + 4;
        textView.setTag(R.id.btn_location,
                new int[]{savedPadLeft, savedPadTop});
        textView.setTag(finalBgColor); // bg color
        textView.setTag(R.id.btn_add_sticker, borderStyle);

// ✅ post() માં apply — layout ready પછી
        final int fPadL  = savedPadLeft;
        final int fPadT  = savedPadTop;
        final int fPadR  = savedPadRight;
        final int fPadB  = savedPadBottom;
        final int fBorderStyle = borderStyle;
        final int fBgColor     = bgColor;
        final float fStrokeW   = strokeWidth;
        final int fBorderWidth  = borderWidth;
        final int fBorderColor  = borderColor;
        final int fBorderCorner = borderCorner;

        textView.post(() -> {
            // Border apply
            GradientDrawable gd2 = new GradientDrawable();
            gd2.setColor(fBgColor);
            if (fBorderWidth > 0) {
                gd2.setCornerRadius(dpToPx(fBorderCorner));
                switch (fBorderStyle) {
                    case 0: gd2.setStroke(dpToPx(fBorderWidth), fBorderColor); break;
                    case 1: gd2.setStroke(dpToPx(fBorderWidth), fBorderColor, dpToPx(8), dpToPx(4)); break;
                    case 2: gd2.setStroke(dpToPx(fBorderWidth), fBorderColor, dpToPx(2), dpToPx(4)); break;
                    case 3: gd2.setStroke(dpToPx(fBorderWidth + 2), fBorderColor); break;
                    default: applyBorderStyle(gd2, fBorderStyle);
                }
            } else {
                applyBorderStyle(gd2, fBorderStyle);
            }
            textView.setBackground(gd2);

            // Padding apply
            int se = (int) Math.ceil(fStrokeW) + 4;
            textView.setPadding(
                    se + fPadL,
                    se + fPadT,
                    se + fPadR,
                    se + fPadB);

            textView.requestLayout();
        });


// ✅ Padding tag store — next save માટે
        textView.setTag(R.id.btn_location,
                new int[]{savedPadLeft, savedPadTop});


        float lineMult = (float) obj.optDouble("lineSpacingMult", 1.0f);
        textView.setLineSpacing(0f, lineMult);

        float letterSp = (float) obj.optDouble("letterSpacing", 0f);
        textView.setLetterSpacing(letterSp);

        float alpha = (float) obj.optDouble("alpha", 1.0f);
        textView.setAlpha(alpha);

        float scaleX = (float) obj.optDouble("scaleX", 1.0f);
        float scaleY = (float) obj.optDouble("scaleY", 1.0f);
        textView.setScaleX(scaleX);
        textView.setScaleY(scaleY);

        float shadowR = (float) obj.optDouble("shadowRadius", 0f);
        if (shadowR > 0) {
            float shadowDx = (float) obj.optDouble("shadowDx", 3f);
            float shadowDy = (float) obj.optDouble("shadowDy", 3f);
            int shadowC = obj.optInt("shadowColor",
                    Color.parseColor("#80000000"));
            textView.setShadowLayer(shadowR, shadowDx, shadowDy, shadowC);
        }

        // Save માં ઉમેરો:
        obj.put("isBold", textView.getTypeface() != null && textView.getTypeface().isBold());
        obj.put("isItalic", textView.getTypeface() != null && textView.getTypeface().isItalic());
        obj.put("isUnderline", (textView.getPaintFlags() & Paint.UNDERLINE_TEXT_FLAG) != 0);
        obj.put("isStrike", (textView.getPaintFlags() & Paint.STRIKE_THRU_TEXT_FLAG) != 0);
        obj.put("letterSpace", textView.getLetterSpacing());
        obj.put("lineSpace", textView.getLineSpacingMultiplier());
        float rotation = (float) obj.optDouble("rotation", 0f);
        textView.setRotation(rotation);
// Load માં ઉમેરો:
        applyAllStyles(textView, obj.optBoolean("isBold", false), obj.optBoolean("isItalic", false), obj.optBoolean("isUnderline", false), obj.optBoolean("isStrike", false), obj.optString("fontKey", "DEFAULT"), (float) obj.optDouble("letterSpace", 0f), (float) obj.optDouble("lineSpace", 1.0f), obj.optString("textTransform", "NONE"));

        mainLayout.post(() -> {
            int savedIndex = obj.optInt("layerIndex", -1);
            if (savedIndex > 0) {
                // bounds check
                int max = mainLayout.getChildCount() - 1;
                int targetIdx = Math.min(savedIndex, max);
                mainLayout.removeView(textView);   // અથવા sticker
                mainLayout.addView(textView, targetIdx);
            }
        });





        textView.setStrokeWidth(strokeWidth);
        textView.setStrokeColor(strokeColor);

        applyTouchListener(textView);
        mainLayout.addView(textView);

        mainLayout.post(() -> {
            try {
                float sw = main_image_view.getWidth();
                float sh = main_image_view.getHeight();

                if (obj.has("sizePercent")) {
                    float sizePx = (float) (obj.getDouble("sizePercent") * sw / 100f);
                    textView.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, sizePx);
                } else {
                    textView.setTextSize(obj.optInt("size", 20));
                }

                textView.post(() -> {
                    try {
                        // આ કરો:
                        float imgLeft = main_image_view.getLeft();
                        float imgTop  = main_image_view.getTop();
                        float imgW    = main_image_view.getWidth();
                        float imgH    = main_image_view.getHeight();

                        float centerX = imgLeft + (float)(obj.getDouble("xPercent") * imgW / 100f);
                        float centerY = imgTop  + (float)(obj.getDouble("yPercent") * imgH / 100f);
                        textView.setX(centerX - textView.getWidth()  / 2f);
                        textView.setY(centerY - textView.getHeight() / 2f);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                });

            } catch (JSONException e) {
                e.printStackTrace();
            }
        });

       *//* mainLayout.post(() -> {
            try {
                float sw = mainLayout.getWidth();
                float sh = mainLayout.getHeight();

                float centerX = (float) (obj.getDouble("xPercent") * sw / 100f);
                float centerY = (float) (obj.getDouble("yPercent") * sh / 100f);

                textView.setX(centerX - textView.getWidth() / 2f);
                textView.setY(centerY - textView.getHeight() / 2f);

                if (obj.has("sizePercent")) {
                    float sizePx = (float) (obj.getDouble("sizePercent") * sw / 100);
                    textView.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, sizePx);
                } else {
                    textView.setTextSize(obj.optInt("size", 20));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });*//*

    }*/


    private void addNewTextViewFromLoad(JSONObject obj) throws JSONException {

        final StrokeTextView textView = new StrokeTextView(this);
        textView.setText(obj.getString("text"));
        textView.setTextColor(obj.getInt("color"));

        // ── Bold / Underline
        boolean bold = obj.optBoolean("isBold", false);
        boolean underline = obj.optBoolean("isUnderline", false);
        applyStyleToTextView(textView, bold, underline);

        // ── Stroke
        float strokeWidth = (float) obj.optDouble("strokeWidth", 0f);
        int strokeColor = obj.optInt("strokeColor", Color.BLACK);
        textView.setStrokeWidth(strokeWidth);
        textView.setStrokeColor(strokeColor);

        // ── BG Color + Border Style
        int bgColor = obj.optInt("bgColor", Color.TRANSPARENT);
        int borderStyle = obj.optInt("borderStyle", 0);

        // ── Full border info load
        int borderWidth  = obj.optInt("borderWidth",  0);
        int borderColor  = obj.optInt("borderColor",  Color.TRANSPARENT);
        int borderCorner = obj.optInt("borderCorner", 0);

        // ── tv_border_info tag set karo jethi restore thay
        if (borderWidth > 0) {
            textView.setTag(R.id.tv_border_info,
                    new int[]{borderWidth, borderColor, borderCorner, borderStyle});
        }

        // ── Padding — JSON માંથી read
        int savedPadLeft = obj.optInt("paddingLeft", 20);
        int savedPadTop = obj.optInt("paddingTop", 20);
        int savedPadRight = obj.optInt("paddingRight", savedPadLeft);
        int savedPadBottom = obj.optInt("paddingBottom", savedPadTop);

        // ── Tags store — save/restore માટે
        textView.setTag(bgColor);
        textView.setTag(R.id.btn_add_sticker, borderStyle);
        textView.setTag(R.id.btn_location, new int[]{savedPadLeft, savedPadTop});

        // ── Other styles
        int gravity = obj.optInt("textGravity", android.view.Gravity.CENTER);
        textView.setGravity(gravity);

        float lineMult = (float) obj.optDouble("lineSpacingMult", 1.0f);
        textView.setLineSpacing(0f, lineMult);

        float letterSp = (float) obj.optDouble("letterSpacing", 0f);
        textView.setLetterSpacing(letterSp);

        float alpha = (float) obj.optDouble("alpha", 1.0f);
        textView.setAlpha(alpha);

        float scaleX = (float) obj.optDouble("scaleX", 1.0f);
        float scaleY = (float) obj.optDouble("scaleY", 1.0f);
        textView.setScaleX(scaleX);
        textView.setScaleY(scaleY);

        float rotation = (float) obj.optDouble("rotation", 0f);
        textView.setRotation(rotation);

        // ── Shadow
        float shadowR = (float) obj.optDouble("shadowRadius", 0f);
        if (shadowR > 0) {
            float shadowDx = (float) obj.optDouble("shadowDx", 3f);
            float shadowDy = (float) obj.optDouble("shadowDy", 3f);
            int shadowC = obj.optInt("shadowColor",
                    Color.parseColor("#80000000"));
            textView.setShadowLayer(shadowR, shadowDx, shadowDy, shadowC);
        }

        // ── All styles (bold, italic, underline, strike, font, etc.)
        obj.put("isBold", textView.getTypeface() != null && textView.getTypeface().isBold());
        obj.put("isItalic", textView.getTypeface() != null && textView.getTypeface().isItalic());
        obj.put("isUnderline", (textView.getPaintFlags() & Paint.UNDERLINE_TEXT_FLAG) != 0);
        obj.put("isStrike", (textView.getPaintFlags() & Paint.STRIKE_THRU_TEXT_FLAG) != 0);
        obj.put("letterSpace", textView.getLetterSpacing());
        obj.put("lineSpace", textView.getLineSpacingMultiplier());

        applyAllStyles(
                textView,
                obj.optBoolean("isBold", false),
                obj.optBoolean("isItalic", false),
                obj.optBoolean("isUnderline", false),
                obj.optBoolean("isStrike", false),
                obj.optString("fontKey", "DEFAULT"),
                (float) obj.optDouble("letterSpace", 0f),
                (float) obj.optDouble("lineSpace", 1.0f),
                obj.optString("textTransform", "NONE")
        );

        // ── Arc mode
        textView.setArcMode(false);
        boolean isArcMode = obj.optBoolean("isArcMode", false);
        if (isArcMode) {
            textView.setArcMode(true);
            textView.setArcAngle((float) obj.optDouble("arcAngle", 180f));
            textView.setRadius((float) obj.optDouble("arcRadius", 150f));
            textView.setArcUp(obj.optBoolean("arcUp", true));
        }

        // ── Locked state
        boolean isLocked = obj.optBoolean("isLocked", false);

        // ── Touch listener
        applyTouchListener(textView);

        // ── Layout params
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        textView.setLayoutParams(params);

        // ── View add to layout
        mainLayout.addView(textView);

        // ── final variables for post()
        final int fBgColor = bgColor;
        final int fBorderStyle = borderStyle;
        final int fBorderWidth  = borderWidth;
        final int fBorderColor  = borderColor;
        final int fBorderCorner = borderCorner;
        final int fPadLeft = savedPadLeft;
        final int fPadTop = savedPadTop;
        final int fPadRight = savedPadRight;
        final int fPadBottom = savedPadBottom;
        final float fStrokeW = strokeWidth;
        final int fStrokeColor = strokeColor;
        final boolean fIsLocked = isLocked;

        // ── post() — layout ready પછી apply
        textView.post(() -> {

            // ── Border + BG apply
            GradientDrawable gd = new GradientDrawable();
            gd.setColor(fBgColor);
            if (fBorderWidth > 0) {
                gd.setCornerRadius(dpToPx(fBorderCorner));
                switch (fBorderStyle) {
                    case 0: gd.setStroke(dpToPx(fBorderWidth), fBorderColor); break;
                    case 1: gd.setStroke(dpToPx(fBorderWidth), fBorderColor, dpToPx(8), dpToPx(4)); break;
                    case 2: gd.setStroke(dpToPx(fBorderWidth), fBorderColor, dpToPx(2), dpToPx(4)); break;
                    case 3: gd.setStroke(dpToPx(fBorderWidth + 2), fBorderColor); break;
                    default: applyBorderStyle(gd, fBorderStyle);
                }
            } else {
                applyBorderStyle(gd, fBorderStyle);
            }
            textView.setBackground(gd);

            // ── Stroke re-apply (gradient safe)
            textView.setStrokeWidth(fStrokeW);
            textView.setStrokeColor(fStrokeColor);

            // ── Padding apply
            int se = (int) Math.ceil(fStrokeW) + 4;
            textView.setPadding(
                    se + fPadLeft,
                    se + fPadTop,
                    se + fPadRight,
                    se + fPadBottom);

            textView.requestLayout();

            // ── Lock apply
            if (fIsLocked) {
                lockedViews.add(textView);
                updateLockIcon(textView);
                textView.setOnTouchListener((v, event) -> {
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        showLockPopup(textView);
                    }
                    return true;
                });
            }
        });

        // ── Gradient load
        String gradColors = obj.optString("gradientColors", "");
        if (!gradColors.isEmpty()) {
            String[] parts = gradColors.split(",");
            if (parts.length >= 3) {
                try {
                    int gc1 = Integer.parseInt(parts[0].trim());
                    int gc2 = Integer.parseInt(parts[1].trim());
                    String gdir = parts[2].trim();

                    textView.setTag(R.id.tv_move_speed,
                            gc1 + "," + gc2 + "," + gdir);

                    textView.post(() -> {
                        applyGradientToView(textView, gc1, gc2, gdir);
                        textView.post(() -> {
                            textView.setStrokeWidth(fStrokeW);
                            textView.setStrokeColor(fStrokeColor);
                            textView.invalidate();
                        });
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        // ── BG Image load
        String bgImageUri = obj.optString("bgImageUri", "");
        if (!bgImageUri.isEmpty()) {
            textView.setTag(R.id.btn_sticker_gallery, bgImageUri);
            mainLayout.post(() -> applyTextBgImage(
                    Uri.parse(bgImageUri), textView));
        }

        // ── Layer index restore
        mainLayout.post(() -> {
            int savedIndex = obj.optInt("layerIndex", -1);
            if (savedIndex > 0) {
                int max = mainLayout.getChildCount() - 1;
                int targetIdx = Math.min(savedIndex, max);
                mainLayout.removeView(textView);
                mainLayout.addView(textView, targetIdx);
            }
        });

        // ── Size + Position — post() માં
        // ── Size + Position — post() માં
        mainLayout.post(() -> {
            try {
                float imgW = main_image_view.getWidth();
                float imgH = main_image_view.getHeight();

                if (imgW <= 0 || imgH <= 0) {
                    // retry
                    mainLayout.postDelayed(() -> {
                        try {
                            float iW2 = main_image_view.getWidth();
                            float iH2 = main_image_view.getHeight();
                            if (iW2 <= 0) iW2 = mainLayout.getWidth();
                            if (iH2 <= 0) iH2 = mainLayout.getHeight();

                            // ✅ final variables — lambda માટે
                            final float fiW = iW2;
                            final float fiH = iH2;

                            float sizePx = obj.has("sizePercent")
                                    ? (float) (obj.getDouble("sizePercent") * fiW / 100f)
                                    : obj.optInt("size", 20);
                            textView.setTextSize(
                                    android.util.TypedValue.COMPLEX_UNIT_PX,
                                    sizePx);

                            float imgLeft2 = main_image_view.getLeft();
                            float imgTop2 = main_image_view.getTop();

                            // ✅ final variables
                            final float fImgLeft2 = imgLeft2;
                            final float fImgTop2 = imgTop2;

                            textView.post(() -> {
                                try {
                                    float cx = fImgLeft2 + (float) (obj.getDouble("xPercent") * fiW / 100f);
                                    float cy = fImgTop2 + (float) (obj.getDouble("yPercent") * fiH / 100f);
                                    textView.setX(cx - textView.getWidth() / 2f);
                                    textView.setY(cy - textView.getHeight() / 2f);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            });

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }, 300);
                    return;
                }

                // ── Size set
                // ✅ final variables
                final float fImgW = imgW;
                final float fImgH = imgH;

                float sizePx = obj.has("sizePercent")
                        ? (float) (obj.getDouble("sizePercent") * fImgW / 100f)
                        : obj.optInt("size", 20);
                textView.setTextSize(
                        android.util.TypedValue.COMPLEX_UNIT_PX,
                        sizePx);

                final float fImgLeft = main_image_view.getLeft();
                final float fImgTop = main_image_view.getTop();

                // ── Position set
                textView.post(() -> {
                    try {
                        float cx = fImgLeft + (float) (obj.getDouble("xPercent") * fImgW / 100f);
                        float cy = fImgTop + (float) (obj.getDouble("yPercent") * fImgH / 100f);
                        textView.setX(cx - textView.getWidth() / 2f);
                        textView.setY(cy - textView.getHeight() / 2f);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                });

            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
    }


    private void addNewTextView() {
        final StrokeTextView textView = new StrokeTextView(this);
        textView.setText("નવું લખાણ");
        textView.setTextSize(20);
        textView.setTextColor(Color.BLACK);
        textView.setPadding(20, 20, 20, 20);

        // Default - No Stroke
        textView.setStrokeWidth(0f);
        textView.setStrokeColor(Color.BLACK);

        GradientDrawable gd = new GradientDrawable();
        gd.setColor(Color.TRANSPARENT);
        gd.setStroke(0, Color.TRANSPARENT); // ← border 0
        //gd.setStroke(4, Color.BLACK);
        gd.setCornerRadius(12f);
        textView.setBackground(gd);
        textView.setTag(Color.TRANSPARENT);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        textView.setLayoutParams(params);

        applyTouchListener(textView);
        mainLayout.addView(textView);
        selectTextView(textView);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void applyTouchListener(final StrokeTextView textView) {
        textView.setOnTouchListener(new View.OnTouchListener() {


            private float startX, startY;

            // ── Multi-select tracking — onTouch બહાર
            private long msDownTime = 0;
            private boolean msLongPress = false;
            private float msDownX = 0, msDownY = 0;

            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if (lockedViews.contains(view)) {
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        // ✅ Locked text — select + controls show
                        StrokeTextView tv = (StrokeTextView) view;
                        selectTextView(tv);
                        if (tv.isArcMode()) {
                            showArcTextPopup(tv);
                        } else {
                            
                            showSelectionControlsForText(tv);
                        }
                    }
                    return true;
                }

                // ── Multi-select mode
                if (isMultiSelectMode) {

                    switch (event.getActionMasked()) {

                        case MotionEvent.ACTION_DOWN:
                            msDownTime = System.currentTimeMillis();
                            msLongPress = false;
                            msDownX = event.getRawX();
                            msDownY = event.getRawY();

                            groupMoveStartX = event.getRawX();
                            groupMoveStartY = event.getRawY();
                            groupStartPositions.clear();
                            for (View sel : selectedViews) {
                                groupStartPositions.add(new float[]{sel.getX(), sel.getY()});
                            }
                            isGroupMoving = true;
                            break;

                        case MotionEvent.ACTION_MOVE:
                            long held = System.currentTimeMillis() - msDownTime;
                            float moved = (float) Math.sqrt(Math.pow(event.getRawX() - msDownX, 2) + Math.pow(event.getRawY() - msDownY, 2));

                            if (held > 300 || moved > 10) {
                                msLongPress = true;
                            }

                            if (isGroupMoving && msLongPress) {
                                handleGroupMove(event);
                            }
                            break;

                        case MotionEvent.ACTION_UP:
                            isGroupMoving = false;
                            float endX2 = event.getRawX();
                            float endY2 = event.getRawY();
                            // ✅ msDownX/Y use — startX/Y નહીં
                            float dist3 = (float) Math.sqrt(Math.pow(endX2 - msDownX, 2) + Math.pow(endY2 - msDownY, 2));


                            if (!msLongPress && dist3 < 10) {
                                if ("TEXT".equals(currentFilter)) {
                                    StrokeTextView tv = (StrokeTextView) view;

                                    if (tv.isArcMode()) {
                                        showArcTextPopup(tv);      // ✅ Arc text click = Arc popup
                                    } else {
                                        /*    // ✅ Normal text click = old popup*/
                                        showSelectionControlsForText(tv);
                                    }
                                } else {
                                    if (selectedViews.contains(view)) {
                                        selectedViews.remove(view);
                                        restoreViewBorder(view);
                                        selectedOriginalSizes.clear();
                                        for (View s : selectedViews)
                                            saveOriginalSize(s);
                                    } else {
                                        selectedViews.add(view);
                                        applySelectionBorder(view);
                                        saveOriginalSize(view);
                                        groupStartPositions.add(new float[]{view.getX(), view.getY()});
                                    }
                                    if (seekMultiSize != null) seekMultiSize.setProgress(50);
                                    if (tvSizeLabel != null) tvSizeLabel.setText("+0.0sp");
                                    updateMultiSelectBtnLabel();
                                }
                            }
                            break;

                    }
                    return true;
                }

                // ── Normal single touch
                rotationGestureDetector.onTouchEvent(event);
                scaleGestureDetector.onTouchEvent(event);

                if (event.getPointerCount() > 1) {
                    selectTextView((StrokeTextView) view);
                    return true;
                }

                switch (event.getActionMasked()) {

                    case MotionEvent.ACTION_DOWN:
                        dX = view.getX() - event.getRawX();
                        dY = view.getY() - event.getRawY();
                        startX = event.getRawX();
                        startY = event.getRawY();
                        try {
                            if (frameImageControlsPopup != null && frameImageControlsPopup.isShowing()) {
                                frameImageControlsPopup.dismiss();
                            }
                        } catch (Exception ignored) {
                        }
                        selectTextView((StrokeTextView) view);
                        return true;

                    case MotionEvent.ACTION_MOVE:
                        float newX = event.getRawX() + dX;
                        float newY = event.getRawY() + dY;

                        float minX = main_image_view.getLeft();
                        float maxX = main_image_view.getRight() - view.getWidth();
                        float minY = main_image_view.getTop();
                        float maxY = main_image_view.getBottom() - view.getHeight();

                        newX = Math.max(minX, Math.min(newX, maxX));
                        newY = Math.max(minY, Math.min(newY, maxY));

                        view.setX(newX);
                        view.setY(newY);
                        break;

                    case MotionEvent.ACTION_UP:
                        float endX = event.getRawX();
                        float endY = event.getRawY();
                        float dist2 = (float) Math.sqrt(Math.pow(endX - startX, 2) + Math.pow(endY - startY, 2));

                        if (dist2 < 10) {
                            if (isMultiSelectMode) {
                                if ("TEXT".equals(currentFilter)) {
                                    // ✅ TEXT filter — edit toolbar
                                    StrokeTextView tv = (StrokeTextView) view;

                                    if (tv.isArcMode()) {
                                        showArcTextPopup(tv);      // ✅ Arc text click = Arc popup
                                    } else {
                                        
                                        showSelectionControlsForText(tv);  // ✅ Normal text click = old popup
                                    }
                                } else {
                                    // ✅ Normal multi-select toggle
                                    if (selectedViews.contains(view)) {
                                        selectedViews.remove(view);
                                        restoreViewBorder(view);
                                        selectedOriginalSizes.clear();
                                        for (View s : selectedViews)
                                            saveOriginalSize(s);
                                    } else {
                                        selectedViews.add(view);
                                        applySelectionBorder(view);
                                        saveOriginalSize(view);
                                        groupStartPositions.add(new float[]{view.getX(), view.getY()});
                                    }
                                    if (seekMultiSize != null) seekMultiSize.setProgress(50);
                                    if (tvSizeLabel != null) tvSizeLabel.setText("+0.0sp");
                                    updateMultiSelectBtnLabel();
                                }

                                break;
                            } else {
                                // ✅ Normal mode

                                
                                showSelectionControlsForText((StrokeTextView) view);
                            }
                        }
                        break;

                }
                return true;
            }
        });
    }

    private int getBackgroundColor(TextView textView) {
        Drawable background = textView.getBackground();
        if (background instanceof ColorDrawable) {
            return ((ColorDrawable) background).getColor();
        }
        return Color.TRANSPARENT;
    }

    private int getStoredBackgroundColor(TextView textView) {
        Object tag = textView.getTag();
        if (tag instanceof Integer) {
            return (Integer) tag;
        }
        return getBackgroundColor(textView);
    }

    private void hideAllPopupWindows() {

        try {
            if (selectionControlsPopup != null && selectionControlsPopup.isShowing()) {
                selectionControlsPopup.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        selectionControlsPopup = null;

        try {
            if (currentStickerToolbarPopup != null && currentStickerToolbarPopup.isShowing()) {
                currentStickerToolbarPopup.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        currentStickerToolbarPopup = null;

        try {
            if (arcTextPopup != null && arcTextPopup.isShowing()) {
                arcTextPopup.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        arcTextPopup = null;

        try {
            if (multiSelectPopup != null && multiSelectPopup.isShowing()) {
                multiSelectPopup.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        multiSelectPopup = null;
    }


    private void showFloatingToolbar(final StrokeTextView targetText) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.layout_text_toolbar, null);

        popupView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        int popupHeight = popupView.getMeasuredHeight();

        final PopupWindow popupWindow = new PopupWindow(
                popupView,
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                true
        );

        LinearLayout pageLiner = findViewById(R.id.page_liner);
        if (pageLiner != null) {
            pageLiner.setVisibility(View.GONE);
            popupWindow.setOnDismissListener(() ->
                    pageLiner.setVisibility(View.VISIBLE));
        }

        // ── Lock button
        TextView btnLock = popupView.findViewById(R.id.btn_pop_lock);
        if (btnLock != null) {
            boolean isLocked = lockedViews.contains(targetText);
            btnLock.setText(isLocked ? "🔒\nLocked" : "🔓\nLock");
            btnLock.setBackgroundColor(isLocked
                    ? Color.parseColor("#FFCDD2")
                    : Color.parseColor("#E8F5E9"));

            btnLock.setOnClickListener(v -> {
                popupWindow.dismiss();
                toggleLock(targetText);
            });
        }

        // ── Edit Text button
        View btnEdit = popupView.findViewById(R.id.btn_pop_edit_text);
        if (btnEdit != null) {
            btnEdit.setOnClickListener(v -> {
                popupWindow.dismiss();
                showEditTextDialog(targetText);
            });
        }

        // ── Delete button
        View btnDelete = popupView.findViewById(R.id.btn_pop_delete);
        if (btnDelete != null) {
            btnDelete.setOnClickListener(v -> {
                new android.app.AlertDialog.Builder(this)
                        .setTitle("Delete Text")
                        .setMessage("\"" + targetText.getText().toString()
                                + "\" delete કરવો?")
                        .setPositiveButton("Delete", (d, w) -> {

                            popupWindow.dismiss();

                            // ── Deleted list માં add
                            try {
                                JSONObject deletedObj = new JSONObject();
                                deletedObj.put("text",
                                        targetText.getText().toString());
                                deletedObj.put("color",
                                        targetText.getCurrentTextColor());
                                deletedObj.put("size",
                                        targetText.getTextSize()
                                                / getResources()
                                                .getDisplayMetrics()
                                                .scaledDensity);

                                float sw = mainLayout.getWidth();
                                float sh = mainLayout.getHeight();
                                deletedObj.put("xPercent",
                                        sw == 0 ? 0
                                                : (targetText.getX() / sw) * 100);
                                deletedObj.put("yPercent",
                                        sh == 0 ? 0
                                                : (targetText.getY() / sh) * 100);
                                deletedObj.put("bgColor",
                                        getStoredBackgroundColor(targetText));
                                deletedObj.put("rotation",
                                        targetText.getRotation());

                                Object borderTag = targetText
                                        .getTag(R.id.btn_add_sticker);
                                deletedObj.put("borderStyle",
                                        borderTag instanceof Integer
                                                ? (int) borderTag : 0);

                                deletedObj.put("strokeWidth",
                                        targetText.getStrokeWidth());
                                deletedObj.put("strokeColor",
                                        targetText.getStrokeColor());
                                deletedObj.put("isBold",
                                        targetText.getTypeface() != null
                                                && targetText.getTypeface()
                                                .isBold());
                                deletedObj.put("isUnderline",
                                        (targetText.getPaintFlags()
                                                & Paint.UNDERLINE_TEXT_FLAG) != 0);
                                deletedObj.put("isArcMode",
                                        targetText.isArcMode());
                                if (targetText.isArcMode()) {
                                    deletedObj.put("arcAngle",
                                            targetText.getArcAngle());
                                    deletedObj.put("arcRadius",
                                            targetText.getRadius());
                                    deletedObj.put("arcUp",
                                            targetText.isArcUp());
                                }

                                // ── Gradient save
                                Object gradTag = targetText
                                        .getTag(R.id.tv_move_speed);
                                if (gradTag != null
                                        && !gradTag.toString().isEmpty()) {
                                    deletedObj.put("gradientColors",
                                            gradTag.toString());
                                }

                                deletedTextsList.add(deletedObj);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            // ── Layout માંથી remove
                            mainLayout.removeView(targetText);

                            // ── Selection reset
                            if (currentlySelectedTextView == targetText) {
                                currentlySelectedTextView = null;
                            }
                            if (currentlySelectedView == targetText) {
                                currentlySelectedView = null;
                            }

                            // ── Locked views cleanup
                            lockedViews.remove(targetText);

                            // ── Controls dismiss
                            dismissSelectionControls();

                            // ── Panel refresh
                            refreshLockedLayersPanel();

                            exportToJson();

                            Toast.makeText(this,
                                    "🗑 Text delete થઈ ગયો!",
                                    Toast.LENGTH_SHORT).show();
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            });
        }

        // ── Popup position
        int[] location = new int[2];
        targetText.getLocationOnScreen(location);
        int x = location[0];
        int y = location[1] - popupHeight - 40;

        if (y < 100) {
            y = location[1] + targetText.getHeight() + 20;
        }

        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(
                new android.graphics.drawable.ColorDrawable(
                        Color.TRANSPARENT));

        popupWindow.showAtLocation(targetText,
                Gravity.NO_GRAVITY, x, y);
    }

    private void bringViewToFront(View view) {
        if (view == null) return;
        ViewGroup parent = (ViewGroup) view.getParent();
        if (parent != null) {
            parent.removeView(view);
        }
        mainLayout.addView(view);
    }

    private void bringViewOneLayerUp(View view) {
        if (view == null) return;
        int currentIndex = mainLayout.indexOfChild(view);
        int total = mainLayout.getChildCount();

        if (currentIndex == -1 || currentIndex >= total - 1) {
            Toast.makeText(this, "Already at top!", Toast.LENGTH_SHORT).show();
            return;
        }

        mainLayout.removeView(view);
        mainLayout.addView(view, currentIndex + 1);
        exportToJson();
    }

    private void sendViewOneLayerDown(View view) {
        if (view == null) return;
        int currentIndex = mainLayout.indexOfChild(view);
        int minIndex = getMainImageViewIndex() + 1;

        if (currentIndex == -1 || currentIndex <= minIndex) {
            Toast.makeText(this, "Already at bottom!", Toast.LENGTH_SHORT).show();
            return;
        }

        mainLayout.removeView(view);
        mainLayout.addView(view, currentIndex - 1);
        exportToJson();
    }

    private void sendViewToBack(View view) {
        if (view == null) return;
        int minIndex = getMainImageViewIndex() + 1;

        // ✅ Parent check — removeView પહેલા
        ViewGroup parent = (ViewGroup) view.getParent();
        if (parent != null) {
            parent.removeView(view);
        }

        // ✅ bounds check
        int total = mainLayout.getChildCount();
        int targetIndex = Math.min(minIndex, total);
        mainLayout.addView(view, targetIndex);
        exportToJson();
    }


    // ── main_image_view નો index
    private int getMainImageViewIndex() {
        return mainLayout.indexOfChild(main_image_view);
    }

    private void showTextFormatDialog(final StrokeTextView targetText) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Text Format Settings");

        ScrollView scrollView = new ScrollView(this);
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(40, 30, 40, 30);
        scrollView.addView(layout);

        // ── LIVE PREVIEW ─────────────────────────────────────────
        final StrokeTextView preview = new StrokeTextView(this);
        preview.setText(targetText.getText());
        preview.setTextColor(targetText.getCurrentTextColor());
        preview.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, targetText.getTextSize());
        preview.setGravity(Gravity.CENTER);
        preview.setPadding(20, 20, 20, 20);
        preview.setStrokeColor(targetText.getStrokeColor());
        preview.setStrokeWidth(targetText.getStrokeWidth());

        // Current State
        final boolean[] isBold = {targetText.getTypeface() != null && targetText.getTypeface().isBold()};
        final boolean[] isItalic = {targetText.getTypeface() != null && targetText.getTypeface().isItalic()};
        final boolean[] isUnderline = {(targetText.getPaintFlags() & Paint.UNDERLINE_TEXT_FLAG) != 0};
        final boolean[] isStrike = {(targetText.getPaintFlags() & Paint.STRIKE_THRU_TEXT_FLAG) != 0};
        final int[] textAlign = {targetText.getGravity()};
        final String[] selFont = {"DEFAULT"};
        final float[] letterSpace = {targetText.getLetterSpacing()};
        final float[] lineSpace = {targetText.getLineSpacingMultiplier()};
        final String[] textTransform = {"NONE"};

        applyAllStyles(preview, isBold[0], isItalic[0], isUnderline[0], isStrike[0], selFont[0], letterSpace[0], lineSpace[0], textTransform[0]);

        LinearLayout.LayoutParams previewParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 160);
        previewParams.setMargins(0, 0, 0, 20);
        preview.setLayoutParams(previewParams);
        layout.addView(preview);

        // ── 1. SIZE ROW ───────────────────────────────────────────
        addSectionLabel(layout, "TEXT SIZE");
        float initSp = targetText.getTextSize() / getResources().getDisplayMetrics().scaledDensity;
        final TextView lblSize = new TextView(this);
        lblSize.setText("Size: " + (int) initSp + "sp");
        lblSize.setTextSize(13);
        layout.addView(lblSize);

        LinearLayout sizeRow = new LinearLayout(this);
        sizeRow.setOrientation(LinearLayout.HORIZONTAL);
        sizeRow.setGravity(Gravity.CENTER_VERTICAL);

        final Button btnMinus = makeSmallBtn(this, "−");
        final android.widget.SeekBar seekSize = new android.widget.SeekBar(this);
        seekSize.setMin(10);
        seekSize.setMax(80);
        seekSize.setProgress((int) initSp);
        LinearLayout.LayoutParams sp = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        sp.setMargins(8, 0, 8, 0);
        seekSize.setLayoutParams(sp);
        final Button btnPlus = makeSmallBtn(this, "+");

        seekSize.setOnSeekBarChangeListener(new android.widget.SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(android.widget.SeekBar s, int p, boolean f) {
                lblSize.setText("Size: " + p + "sp");
                preview.setTextSize(p);
            }

            public void onStartTrackingTouch(android.widget.SeekBar s) {
            }

            public void onStopTrackingTouch(android.widget.SeekBar s) {
            }
        });
        btnMinus.setOnClickListener(v -> {
            if (seekSize.getProgress() > 10) {
                seekSize.setProgress(seekSize.getProgress() - 2);
                preview.setTextSize(seekSize.getProgress());
                lblSize.setText("Size: " + seekSize.getProgress() + "sp");
            }
        });
        btnPlus.setOnClickListener(v -> {
            if (seekSize.getProgress() < 80) {
                seekSize.setProgress(seekSize.getProgress() + 2);
                preview.setTextSize(seekSize.getProgress());
                lblSize.setText("Size: " + seekSize.getProgress() + "sp");
            }
        });

        sizeRow.addView(btnMinus);
        sizeRow.addView(seekSize);
        sizeRow.addView(btnPlus);
        layout.addView(sizeRow);
        addDivider(layout);

        // ── 2. STYLE BUTTONS ─────────────────────────────────────
        addSectionLabel(layout, "STYLE");
        LinearLayout styleRow1 = new LinearLayout(this);
        styleRow1.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams srp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        srp.setMargins(0, 0, 0, 8);
        styleRow1.setLayoutParams(srp);

        final Button btnBold = makeToggleBtn(this, "B  Bold", isBold[0]);
        final Button btnItalic = makeToggleBtn(this, "I  Italic", isItalic[0]);
        styleRow1.addView(btnBold);
        styleRow1.addView(makeGap(this, 8));
        styleRow1.addView(btnItalic);
        layout.addView(styleRow1);

        LinearLayout styleRow2 = new LinearLayout(this);
        styleRow2.setOrientation(LinearLayout.HORIZONTAL);
        final Button btnUnder = makeToggleBtn(this, "U  Underline", isUnderline[0]);
        final Button btnStrike = makeToggleBtn(this, "S  Strikethrough", isStrike[0]);
        styleRow2.addView(btnUnder);
        styleRow2.addView(makeGap(this, 8));
        styleRow2.addView(btnStrike);
        layout.addView(styleRow2);

        btnBold.setOnClickListener(v -> {
            isBold[0] = !isBold[0];
            toggleBtnUI(btnBold, isBold[0]);
            applyAllStyles(preview, isBold[0], isItalic[0], isUnderline[0], isStrike[0], selFont[0], letterSpace[0], lineSpace[0], textTransform[0]);
        });
        btnItalic.setOnClickListener(v -> {
            isItalic[0] = !isItalic[0];
            toggleBtnUI(btnItalic, isItalic[0]);
            applyAllStyles(preview, isBold[0], isItalic[0], isUnderline[0], isStrike[0], selFont[0], letterSpace[0], lineSpace[0], textTransform[0]);
        });
        btnUnder.setOnClickListener(v -> {
            isUnderline[0] = !isUnderline[0];
            toggleBtnUI(btnUnder, isUnderline[0]);
            applyAllStyles(preview, isBold[0], isItalic[0], isUnderline[0], isStrike[0], selFont[0], letterSpace[0], lineSpace[0], textTransform[0]);
        });
        btnStrike.setOnClickListener(v -> {
            isStrike[0] = !isStrike[0];
            toggleBtnUI(btnStrike, isStrike[0]);
            applyAllStyles(preview, isBold[0], isItalic[0], isUnderline[0], isStrike[0], selFont[0], letterSpace[0], lineSpace[0], textTransform[0]);
        });
        addDivider(layout);

        // ── 3. FONT FAMILY ────────────────────────────────────────
        addSectionLabel(layout, "FONT FAMILY");
        String[][] fontOptions = {{"Default", "DEFAULT"}, {"Serif", "SERIF"}, {"Monospace", "MONOSPACE"}, {"Cursive", "CURSIVE"}, {"Sans Serif", "SANS_SERIF"}};
        LinearLayout fontRow = new LinearLayout(this);
        fontRow.setOrientation(LinearLayout.HORIZONTAL);
        final Button[] fontBtns = new Button[fontOptions.length];
        for (int i = 0; i < fontOptions.length; i++) {
            final String fontKey = fontOptions[i][1];
            final String fontLabel = fontOptions[i][0];
            Button fb = new Button(this);
            fb.setText(fontLabel);
            fb.setTextSize(11);
            LinearLayout.LayoutParams fp = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
            fp.setMargins(2, 0, 2, 0);
            fb.setLayoutParams(fp);
            fb.setBackgroundColor(fontKey.equals(selFont[0]) ? Color.parseColor("#BBDEFB") : Color.LTGRAY);
            final int idx = i;
            fontBtns[i] = fb;
            fb.setOnClickListener(v -> {
                selFont[0] = fontKey;
                for (Button b2 : fontBtns) b2.setBackgroundColor(Color.LTGRAY);
                fb.setBackgroundColor(Color.parseColor("#BBDEFB"));
                applyAllStyles(preview, isBold[0], isItalic[0], isUnderline[0], isStrike[0], selFont[0], letterSpace[0], lineSpace[0], textTransform[0]);
            });
            fontRow.addView(fb);
        }
        layout.addView(fontRow);
        addDivider(layout);

        // ── 4. LETTER SPACING ────────────────────────────────────
        addSectionLabel(layout, "LETTER SPACING");
        final TextView lblLs = new TextView(this);
        lblLs.setText("Spacing: 0");
        lblLs.setTextSize(13);
        layout.addView(lblLs);
        android.widget.SeekBar seekLs = new android.widget.SeekBar(this);
        seekLs.setMax(20);
        seekLs.setProgress(0);
        seekLs.setOnSeekBarChangeListener(new android.widget.SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(android.widget.SeekBar s, int p, boolean f) {
                letterSpace[0] = p * 0.05f;
                lblLs.setText("Spacing: " + p);
                applyAllStyles(preview, isBold[0], isItalic[0], isUnderline[0], isStrike[0], selFont[0], letterSpace[0], lineSpace[0], textTransform[0]);
            }

            public void onStartTrackingTouch(android.widget.SeekBar s) {
            }

            public void onStopTrackingTouch(android.widget.SeekBar s) {
            }
        });
        layout.addView(seekLs);
        addDivider(layout);

        // ── 5. LINE HEIGHT ────────────────────────────────────────
        addSectionLabel(layout, "LINE HEIGHT");
        final TextView lblLh = new TextView(this);
        lblLh.setText("Line Height: 1.0x");
        lblLh.setTextSize(13);
        layout.addView(lblLh);
        android.widget.SeekBar seekLh = new android.widget.SeekBar(this);
        seekLh.setMax(20);
        seekLh.setProgress(0);
        seekLh.setOnSeekBarChangeListener(new android.widget.SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(android.widget.SeekBar s, int p, boolean f) {
                lineSpace[0] = 1.0f + (p * 0.1f);
                lblLh.setText("Line Height: " + String.format("%.1f", lineSpace[0]) + "x");
                applyAllStyles(preview, isBold[0], isItalic[0], isUnderline[0], isStrike[0], selFont[0], letterSpace[0], lineSpace[0], textTransform[0]);
            }

            public void onStartTrackingTouch(android.widget.SeekBar s) {
            }

            public void onStopTrackingTouch(android.widget.SeekBar s) {
            }
        });
        layout.addView(seekLh);
        addDivider(layout);

        // ── 6. TEXT ALIGN ─────────────────────────────────────────
        addSectionLabel(layout, "TEXT ALIGN");
        LinearLayout alignRow = new LinearLayout(this);
        alignRow.setOrientation(LinearLayout.HORIZONTAL);
        String[][] aligns = {{"Left", Gravity.START + ""}, {"Center", Gravity.CENTER + ""}, {"Right", Gravity.END + ""}};
        final Button[] alignBtns = new Button[3];
        for (int i = 0; i < 3; i++) {
            final int grav = Integer.parseInt(aligns[i][1]);
            Button ab = new Button(this);
            ab.setText(aligns[i][0]);
            LinearLayout.LayoutParams ap = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
            ap.setMargins(2, 0, 2, 0);
            ab.setLayoutParams(ap);
            ab.setBackgroundColor(textAlign[0] == grav ? Color.parseColor("#BBDEFB") : Color.LTGRAY);
            alignBtns[i] = ab;
            ab.setOnClickListener(v -> {
                textAlign[0] = grav;
                preview.setGravity(grav);
                for (Button b2 : alignBtns) b2.setBackgroundColor(Color.LTGRAY);
                ab.setBackgroundColor(Color.parseColor("#BBDEFB"));
            });
            alignRow.addView(ab);
        }
        layout.addView(alignRow);
        addDivider(layout);

        // ── 7. TEXT TRANSFORM ─────────────────────────────────────
        addSectionLabel(layout, "TEXT TRANSFORM");
        LinearLayout ttRow = new LinearLayout(this);
        ttRow.setOrientation(LinearLayout.HORIZONTAL);
        String[][] transforms = {{"Normal", "NONE"}, {"UPPER", "UPPER"}, {"lower", "LOWER"}};
        final Button[] ttBtns = new Button[3];
        for (int i = 0; i < 3; i++) {
            final String tKey = transforms[i][1];
            Button tb = new Button(this);
            tb.setText(transforms[i][0]);
            LinearLayout.LayoutParams tp = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
            tp.setMargins(2, 0, 2, 0);
            tb.setLayoutParams(tp);
            tb.setBackgroundColor(i == 0 ? Color.parseColor("#BBDEFB") : Color.LTGRAY);
            ttBtns[i] = tb;
            tb.setOnClickListener(v -> {
                textTransform[0] = tKey;
                for (Button b2 : ttBtns) b2.setBackgroundColor(Color.LTGRAY);
                tb.setBackgroundColor(Color.parseColor("#BBDEFB"));
                applyAllStyles(preview, isBold[0], isItalic[0], isUnderline[0], isStrike[0], selFont[0], letterSpace[0], lineSpace[0], textTransform[0]);
            });
            ttRow.addView(tb);
        }
        layout.addView(ttRow);

        builder.setView(scrollView);

        // ── APPLY ─────────────────────────────────────────────────
        builder.setPositiveButton("Apply", (dialog, which) -> {
            targetText.setTextSize(seekSize.getProgress());
            targetText.setGravity(textAlign[0]);
            applyAllStyles(targetText, isBold[0], isItalic[0], isUnderline[0], isStrike[0], selFont[0], letterSpace[0], lineSpace[0], textTransform[0]);
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    // બધા styles એકસાથે apply
    private void applyAllStyles(TextView tv, boolean bold, boolean italic, boolean underline, boolean strike, String fontKey, float letterSpace, float lineSpace, String transform) {

        // Typeface (Bold + Italic combine)
        Typeface tf;
        if (bold && italic) tf = Typeface.create(getFontFamily(fontKey), Typeface.BOLD_ITALIC);
        else if (bold) tf = Typeface.create(getFontFamily(fontKey), Typeface.BOLD);
        else if (italic) tf = Typeface.create(getFontFamily(fontKey), Typeface.ITALIC);
        else tf = Typeface.create(getFontFamily(fontKey), Typeface.NORMAL);
        tv.setTypeface(tf);

        // Underline + Strikethrough
        int flags = tv.getPaintFlags();
        flags = underline ? flags | Paint.UNDERLINE_TEXT_FLAG : flags & ~Paint.UNDERLINE_TEXT_FLAG;
        flags = strike ? flags | Paint.STRIKE_THRU_TEXT_FLAG : flags & ~Paint.STRIKE_THRU_TEXT_FLAG;
        tv.setPaintFlags(flags);

        // Letter Spacing
        tv.setLetterSpacing(letterSpace);

        // Line Height
        tv.setLineSpacing(0f, lineSpace);

        // Text Transform
        String original = tv.getText().toString();
        switch (transform) {
            case "UPPER":
                tv.setText(original.toUpperCase());
                break;
            case "LOWER":
                tv.setText(original.toLowerCase());
                break;
            default:
                break;
        }
    }

    // Font family string → Typeface family name
    private String getFontFamily(String key) {
        switch (key) {
            case "SERIF":
                return "serif";
            case "MONOSPACE":
                return "monospace";
            case "CURSIVE":
                return "cursive";
            case "SANS_SERIF":
                return "sans-serif";
            default:
                return null; // Default system font
        }
    }

    // Toggle button highlight
    private void toggleBtnUI(Button btn, boolean active) {
        btn.setBackgroundColor(active ? Color.parseColor("#BBDEFB") : Color.LTGRAY);
    }

    // Section Label helper
    private void addSectionLabel(LinearLayout parent, String text) {
        TextView lbl = new TextView(this);
        lbl.setText(text);
        lbl.setTextSize(11);
        lbl.setTextColor(Color.GRAY);
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        p.setMargins(0, 16, 0, 6);
        lbl.setLayoutParams(p);
        parent.addView(lbl);
    }

    // Divider helper
    private void addDivider(LinearLayout parent) {
        View divider = new View(this);
        divider.setBackgroundColor(Color.parseColor("#E0E0E0"));
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1);
        p.setMargins(0, 12, 0, 4);
        divider.setLayoutParams(p);
        parent.addView(divider);
    }

    // Small ± button
    private Button makeSmallBtn(android.content.Context ctx, String text) {
        Button b = new Button(ctx);
        b.setText(text);
        b.setTextSize(18);
        b.setLayoutParams(new LinearLayout.LayoutParams(100, LinearLayout.LayoutParams.WRAP_CONTENT));
        return b;
    }

    // Toggle style button
    private Button makeToggleBtn(android.content.Context ctx, String label, boolean active) {
        Button b = new Button(ctx);
        b.setText(label);
        b.setTextSize(13);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        b.setLayoutParams(lp);
        b.setBackgroundColor(active ? Color.parseColor("#BBDEFB") : Color.LTGRAY);
        return b;
    }

    // Gap spacer
    private View makeGap(android.content.Context ctx, int widthDp) {
        View v = new View(ctx);
        v.setLayoutParams(new LinearLayout.LayoutParams(widthDp, 1));
        return v;
    }


    private void applyStyleToTextView(TextView tv, boolean bold, boolean underline) {
        // Bold
        android.graphics.Typeface tf = bold ? android.graphics.Typeface.DEFAULT_BOLD : android.graphics.Typeface.DEFAULT;
        tv.setTypeface(tf);

        // Underline
        if (underline) {
            tv.setPaintFlags(tv.getPaintFlags() | android.graphics.Paint.UNDERLINE_TEXT_FLAG);
        } else {
            tv.setPaintFlags(tv.getPaintFlags() & (~android.graphics.Paint.UNDERLINE_TEXT_FLAG));
        }
    }


    private void showStrokeDialog(final StrokeTextView targetText) {

        final float[] strokeW = {targetText.getStrokeWidth()};
        final int[] strokeC = {targetText.getStrokeColor() == 0 ? android.graphics.Color.BLACK : targetText.getStrokeColor()};

        // ── Root layout
        int screenW = getResources().getDisplayMetrics().widthPixels;
        android.widget.LinearLayout root = new android.widget.LinearLayout(this);
        root.setOrientation(android.widget.LinearLayout.VERTICAL);
        root.setLayoutParams(new android.view.ViewGroup.LayoutParams(
                screenW, android.view.ViewGroup.LayoutParams.WRAP_CONTENT));

        // ── Drag Handle
        android.widget.LinearLayout dragHandle = new android.widget.LinearLayout(this);
        dragHandle.setOrientation(android.widget.LinearLayout.HORIZONTAL);
        dragHandle.setBackgroundColor(0xFF2A3439);
        dragHandle.setGravity(android.view.Gravity.CENTER_VERTICAL);
        dragHandle.setPadding(dpToPx(10), 0, dpToPx(6), 0);
        dragHandle.setLayoutParams(new android.widget.LinearLayout.LayoutParams(
                android.widget.LinearLayout.LayoutParams.MATCH_PARENT, dpToPx(32)));

        android.widget.TextView dragDots = new android.widget.TextView(this);
        dragDots.setText("⠿");
        dragDots.setTextColor(0xFF9CA3AF);
        dragDots.setTextSize(12);
        dragDots.setGravity(android.view.Gravity.CENTER);
        dragDots.setLayoutParams(new android.widget.LinearLayout.LayoutParams(dpToPx(16),
                android.widget.LinearLayout.LayoutParams.MATCH_PARENT));

        android.widget.TextView dragTitle = new android.widget.TextView(this);
        dragTitle.setText("✏ Stroke");
        dragTitle.setTextColor(0xFFF3F4F6);
        dragTitle.setTextSize(9);
        dragTitle.setTypeface(null, android.graphics.Typeface.BOLD);
        android.widget.LinearLayout.LayoutParams titleLp =
                new android.widget.LinearLayout.LayoutParams(0,
                        android.widget.LinearLayout.LayoutParams.MATCH_PARENT, 1f);
        titleLp.setMarginStart(dpToPx(4));
        dragTitle.setLayoutParams(titleLp);
        dragTitle.setGravity(android.view.Gravity.CENTER_VERTICAL);

        android.widget.TextView btnCloseX = new android.widget.TextView(this);
        btnCloseX.setText("✕");
        btnCloseX.setTextColor(0xFF9CA3AF);
        btnCloseX.setTextSize(12);
        btnCloseX.setGravity(android.view.Gravity.CENTER);
        btnCloseX.setLayoutParams(new android.widget.LinearLayout.LayoutParams(dpToPx(28), dpToPx(22)));
        btnCloseX.setBackgroundColor(0xFF374151);

        dragHandle.addView(dragDots);
        dragHandle.addView(dragTitle);
        dragHandle.addView(btnCloseX);
        root.addView(dragHandle);

        // ── Content
        android.widget.LinearLayout content = new android.widget.LinearLayout(this);
        content.setOrientation(android.widget.LinearLayout.VERTICAL);
        content.setBackgroundColor(0xFF455A64);
        content.setPadding(dpToPx(12), dpToPx(12), dpToPx(12), dpToPx(12));
        root.addView(content);

        // ── Preview
        StrokeTextView preview = new StrokeTextView(this);
        preview.setText(targetText.getText());
        preview.setTextSize(22);
        preview.setTextColor(targetText.getCurrentTextColor());
        preview.setGravity(Gravity.CENTER);
        preview.setBackgroundColor(0xFF37474F);
        preview.setPadding(dpToPx(12), dpToPx(12), dpToPx(12), dpToPx(12));
        preview.setStrokeColor(strokeC[0]);
        preview.setStrokeWidth(strokeW[0]);
        android.widget.LinearLayout.LayoutParams prevLp =
                new android.widget.LinearLayout.LayoutParams(
                        android.widget.LinearLayout.LayoutParams.MATCH_PARENT, dpToPx(70));
        prevLp.setMargins(0, 0, 0, dpToPx(10));
        preview.setLayoutParams(prevLp);
        content.addView(preview);

        // ── Stroke Width Label + SeekBar
        android.widget.LinearLayout widthRow = new android.widget.LinearLayout(this);
        widthRow.setOrientation(android.widget.LinearLayout.HORIZONTAL);
        widthRow.setGravity(android.view.Gravity.CENTER_VERTICAL);
        widthRow.setLayoutParams(new android.widget.LinearLayout.LayoutParams(
                android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
                android.widget.LinearLayout.LayoutParams.WRAP_CONTENT));

        android.widget.TextView lblWidth = new android.widget.TextView(this);
        lblWidth.setText("Width");
        lblWidth.setTextColor(0xFFCFD8DC);
        lblWidth.setTextSize(11);
        lblWidth.setTypeface(null, android.graphics.Typeface.BOLD);
        lblWidth.setLayoutParams(new android.widget.LinearLayout.LayoutParams(dpToPx(40),
                android.widget.LinearLayout.LayoutParams.WRAP_CONTENT));

        android.widget.SeekBar seekWidth = new android.widget.SeekBar(this);
        seekWidth.setMax(40);
        seekWidth.setProgress((int) strokeW[0]);
        android.widget.LinearLayout.LayoutParams swLp =
                new android.widget.LinearLayout.LayoutParams(0,
                        android.widget.LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        swLp.setMargins(dpToPx(4), 0, dpToPx(4), 0);
        seekWidth.setLayoutParams(swLp);

        android.widget.TextView tvWidthVal = new android.widget.TextView(this);
        tvWidthVal.setText(String.valueOf((int) strokeW[0]));
        tvWidthVal.setTextColor(0xFFCFD8DC);
        tvWidthVal.setTextSize(11);
        tvWidthVal.setLayoutParams(new android.widget.LinearLayout.LayoutParams(dpToPx(24),
                android.widget.LinearLayout.LayoutParams.WRAP_CONTENT));
        tvWidthVal.setGravity(android.view.Gravity.END);

        widthRow.addView(lblWidth);
        widthRow.addView(seekWidth);
        widthRow.addView(tvWidthVal);
        android.widget.LinearLayout.LayoutParams wrLp =
                new android.widget.LinearLayout.LayoutParams(
                        android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
                        android.widget.LinearLayout.LayoutParams.WRAP_CONTENT);
        wrLp.setMargins(0, 0, 0, dpToPx(10));
        widthRow.setLayoutParams(wrLp);
        content.addView(widthRow);

        seekWidth.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onStartTrackingTouch(SeekBar s) {}
            @Override public void onStopTrackingTouch(SeekBar s) {}
            @Override public void onProgressChanged(SeekBar s, int progress, boolean fromUser) {
                strokeW[0] = progress;
                tvWidthVal.setText(String.valueOf(progress));
                preview.setStrokeWidth(progress);
                preview.setStrokeColor(strokeC[0]);
                preview.invalidate();
                // ✅ Runtime apply
                targetText.setStrokeWidth(progress);
                targetText.setStrokeColor(strokeC[0]);
                targetText.invalidate();
            }
        });

        // ── Color Label
        android.widget.TextView lblColor = new android.widget.TextView(this);
        lblColor.setText("🎨 Stroke Color");
        lblColor.setTextColor(0xFFCFD8DC);
        lblColor.setTextSize(11);
        lblColor.setTypeface(null, android.graphics.Typeface.BOLD);
        android.widget.LinearLayout.LayoutParams lcLp =
                new android.widget.LinearLayout.LayoutParams(
                        android.widget.LinearLayout.LayoutParams.WRAP_CONTENT,
                        android.widget.LinearLayout.LayoutParams.WRAP_CONTENT);
        lcLp.setMargins(0, 0, 0, dpToPx(6));
        lblColor.setLayoutParams(lcLp);
        content.addView(lblColor);

        // ── Color rows (Horizontal scroll - 3 rows)
        final int[][] colorRows = {
            {0xFF000000, 0xFFFFFFFF, 0xFFFF0000, 0xFFFF4500, 0xFFFF8C00,
             0xFFFFD700, 0xFFFFFF00, 0xFFADFF2F, 0xFF32CD32, 0xFF00FA9A,
             0xFF00FFFF, 0xFF00BFFF, 0xFF1E90FF, 0xFF0000FF, 0xFF8A2BE2,
             0xFFFF00FF, 0xFFFF1493, 0xFFFF69B4, 0xFFFFA500, 0xFFA52A2A},
            {0xFFF5F5F5, 0xFFE0E0E0, 0xFFC0C0C0, 0xFF9E9E9E, 0xFF757575,
             0xFF616161, 0xFF424242, 0xFF212121, 0xFFFFCDD2, 0xFFF8BBD0,
             0xFFE1BEE7, 0xFFD1C4E9, 0xFFC5CAE9, 0xFFBBDEFB, 0xFFB2EBF2,
             0xFFB2DFDB, 0xFFC8E6C9, 0xFFF0F4C3, 0xFFFFF9C4, 0xFFFFECB3},
            {0xFFB71C1C, 0xFF880E4F, 0xFF4A148C, 0xFF1A237E, 0xFF0D47A1,
             0xFF006064, 0xFF1B5E20, 0xFF33691E, 0xFFF57F17, 0xFFE65100,
             0xFF3E2723, 0xFF263238, 0xFF37474F, 0xFF546E7A, 0xFF78909C,
             0xFF4DB6AC, 0xFF81C784, 0xFFDCE775, 0xFFFFD54F, 0xFFFFB74D},
        };
        String[] rowLabels = {"🌈 Basic", "⬜ Soft", "🎨 Dark"};

        for (int r = 0; r < colorRows.length; r++) {
            android.widget.TextView rowLbl = new android.widget.TextView(this);
            rowLbl.setText(rowLabels[r]);
            rowLbl.setTextColor(0xFFB0BEC5);
            rowLbl.setTextSize(9);
            rowLbl.setTypeface(null, android.graphics.Typeface.BOLD);
            android.widget.LinearLayout.LayoutParams rlLp =
                    new android.widget.LinearLayout.LayoutParams(
                            android.widget.LinearLayout.LayoutParams.WRAP_CONTENT,
                            android.widget.LinearLayout.LayoutParams.WRAP_CONTENT);
            rlLp.setMargins(0, dpToPx(2), 0, dpToPx(2));
            rowLbl.setLayoutParams(rlLp);
            content.addView(rowLbl);

            android.widget.HorizontalScrollView hsv = new android.widget.HorizontalScrollView(this);
            hsv.setHorizontalScrollBarEnabled(false);
            android.widget.LinearLayout.LayoutParams hsvLp =
                    new android.widget.LinearLayout.LayoutParams(
                            android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
                            android.widget.LinearLayout.LayoutParams.WRAP_CONTENT);
            hsvLp.setMargins(0, 0, 0, dpToPx(2));
            hsv.setLayoutParams(hsvLp);

            android.widget.LinearLayout row = new android.widget.LinearLayout(this);
            row.setOrientation(android.widget.LinearLayout.HORIZONTAL);
            row.setPadding(0, dpToPx(2), 0, dpToPx(2));

            for (int c : colorRows[r]) {
                final int fc = c;
                android.view.View btn = new android.view.View(this);
                android.widget.LinearLayout.LayoutParams btnLp =
                        new android.widget.LinearLayout.LayoutParams(dpToPx(34), dpToPx(34));
                btnLp.setMargins(dpToPx(2), 0, dpToPx(2), 0);
                btn.setLayoutParams(btnLp);
                android.graphics.drawable.GradientDrawable btnBg =
                        new android.graphics.drawable.GradientDrawable();
                btnBg.setColor(fc);
                btnBg.setCornerRadius(dpToPx(4));
                btnBg.setStroke(dpToPx(1), 0xFF607D8B);
                btn.setBackground(btnBg);
                btn.setOnClickListener(v -> {
                    strokeC[0] = fc;
                    preview.setStrokeColor(fc);
                    preview.setStrokeWidth(strokeW[0]);
                    preview.invalidate();
                    // ✅ Runtime apply
                    targetText.setStrokeColor(fc);
                    targetText.setStrokeWidth(strokeW[0]);
                    targetText.invalidate();
                });
                row.addView(btn);
            }
            hsv.addView(row);
            content.addView(hsv);
        }

        // ── RGB Sliders
        android.widget.TextView rgbTitle = new android.widget.TextView(this);
        rgbTitle.setText("RGB Custom");
        rgbTitle.setTextColor(0xFFCFD8DC);
        rgbTitle.setTextSize(10);
        rgbTitle.setTypeface(null, android.graphics.Typeface.BOLD);
        android.widget.LinearLayout.LayoutParams rgbTlp =
                new android.widget.LinearLayout.LayoutParams(
                        android.widget.LinearLayout.LayoutParams.WRAP_CONTENT,
                        android.widget.LinearLayout.LayoutParams.WRAP_CONTENT);
        rgbTlp.setMargins(0, dpToPx(8), 0, dpToPx(4));
        rgbTitle.setLayoutParams(rgbTlp);
        content.addView(rgbTitle);

        String[] sliderLabels = {"R", "G", "B"};
        int[] sliderColors2 = {0xFFEF5350, 0xFF66BB6A, 0xFF42A5F5};
        android.widget.SeekBar[] rgbBars = new android.widget.SeekBar[3];
        android.widget.TextView[] rgbVals = new android.widget.TextView[3];

        for (int si = 0; si < 3; si++) {
            final int idx = si;
            android.widget.LinearLayout sliderRow = new android.widget.LinearLayout(this);
            sliderRow.setOrientation(android.widget.LinearLayout.HORIZONTAL);
            sliderRow.setGravity(android.view.Gravity.CENTER_VERTICAL);
            android.widget.LinearLayout.LayoutParams slrLp =
                    new android.widget.LinearLayout.LayoutParams(
                            android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
                            android.widget.LinearLayout.LayoutParams.WRAP_CONTENT);
            slrLp.setMargins(0, dpToPx(2), 0, dpToPx(2));
            sliderRow.setLayoutParams(slrLp);

            android.widget.TextView lbl = new android.widget.TextView(this);
            lbl.setText(sliderLabels[si]);
            lbl.setTextColor(sliderColors2[si]);
            lbl.setTextSize(11);
            lbl.setTypeface(null, android.graphics.Typeface.BOLD);
            lbl.setLayoutParams(new android.widget.LinearLayout.LayoutParams(dpToPx(16),
                    android.widget.LinearLayout.LayoutParams.WRAP_CONTENT));

            android.widget.SeekBar sb = new android.widget.SeekBar(this);
            sb.setMax(255);
            int initVal = si == 0 ? android.graphics.Color.red(strokeC[0])
                        : si == 1 ? android.graphics.Color.green(strokeC[0])
                        : android.graphics.Color.blue(strokeC[0]);
            sb.setProgress(initVal);
            android.widget.LinearLayout.LayoutParams sbLp =
                    new android.widget.LinearLayout.LayoutParams(0,
                            android.widget.LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
            sbLp.setMargins(dpToPx(4), 0, dpToPx(4), 0);
            sb.setLayoutParams(sbLp);
            sb.getProgressDrawable().setColorFilter(sliderColors2[si], android.graphics.PorterDuff.Mode.SRC_IN);
            sb.getThumb().setColorFilter(sliderColors2[si], android.graphics.PorterDuff.Mode.SRC_IN);

            android.widget.TextView val = new android.widget.TextView(this);
            val.setText(String.valueOf(initVal));
            val.setTextColor(0xFFCFD8DC);
            val.setTextSize(10);
            val.setLayoutParams(new android.widget.LinearLayout.LayoutParams(dpToPx(28),
                    android.widget.LinearLayout.LayoutParams.WRAP_CONTENT));
            val.setGravity(android.view.Gravity.END);

            rgbBars[idx] = sb;
            rgbVals[idx] = val;

            sliderRow.addView(lbl);
            sliderRow.addView(sb);
            sliderRow.addView(val);
            content.addView(sliderRow);
        }

        for (int si = 0; si < 3; si++) {
            final int idx = si;
            rgbBars[si].setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override public void onStartTrackingTouch(SeekBar s) {}
                @Override public void onStopTrackingTouch(SeekBar s) {}
                @Override public void onProgressChanged(SeekBar s, int progress, boolean fromUser) {
                    if (!fromUser) return;
                    rgbVals[idx].setText(String.valueOf(progress));
                    int newColor = android.graphics.Color.rgb(
                            rgbBars[0].getProgress(),
                            rgbBars[1].getProgress(),
                            rgbBars[2].getProgress());
                    strokeC[0] = newColor;
                    preview.setStrokeColor(newColor);
                    preview.setStrokeWidth(strokeW[0]);
                    preview.invalidate();
                    // ✅ Runtime apply
                    targetText.setStrokeColor(newColor);
                    targetText.setStrokeWidth(strokeW[0]);
                    targetText.invalidate();
                }
            });
        }

        // ── Action buttons row
        android.widget.LinearLayout actionRow = new android.widget.LinearLayout(this);
        actionRow.setOrientation(android.widget.LinearLayout.HORIZONTAL);
        android.widget.LinearLayout.LayoutParams arLp =
                new android.widget.LinearLayout.LayoutParams(
                        android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
                        android.widget.LinearLayout.LayoutParams.WRAP_CONTENT);
        arLp.setMargins(0, dpToPx(10), 0, 0);
        actionRow.setLayoutParams(arLp);

        android.widget.Button btnRemove = new android.widget.Button(this);
        btnRemove.setText("Remove");
        btnRemove.setTextColor(0xFFFFFFFF);
        btnRemove.setTextSize(11);
        btnRemove.setBackgroundColor(0xFFB71C1C);
        btnRemove.setStateListAnimator(null);
        android.widget.LinearLayout.LayoutParams removeLp =
                new android.widget.LinearLayout.LayoutParams(0,
                        android.widget.LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        removeLp.setMargins(0, 0, dpToPx(3), 0);
        btnRemove.setLayoutParams(removeLp);

        android.widget.Button btnCancel = new android.widget.Button(this);
        btnCancel.setText("Cancel");
        btnCancel.setTextColor(0xFFFFFFFF);
        btnCancel.setTextSize(11);
        btnCancel.setBackgroundColor(0xFF6B7280);
        btnCancel.setStateListAnimator(null);
        android.widget.LinearLayout.LayoutParams cancelLp =
                new android.widget.LinearLayout.LayoutParams(0,
                        android.widget.LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        cancelLp.setMargins(dpToPx(3), 0, dpToPx(3), 0);
        btnCancel.setLayoutParams(cancelLp);

        android.widget.Button btnApply = new android.widget.Button(this);
        btnApply.setText("✅ Apply");
        btnApply.setTextColor(0xFFFFFFFF);
        btnApply.setTextSize(11);
        btnApply.setBackgroundColor(0xFF1565C0);
        btnApply.setStateListAnimator(null);
        android.widget.LinearLayout.LayoutParams applyLp =
                new android.widget.LinearLayout.LayoutParams(0,
                        android.widget.LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        applyLp.setMargins(dpToPx(3), 0, 0, 0);
        btnApply.setLayoutParams(applyLp);

        actionRow.addView(btnRemove);
        actionRow.addView(btnCancel);
        actionRow.addView(btnApply);
        content.addView(actionRow);

        // ── PopupWindow
        android.widget.PopupWindow popup = new android.widget.PopupWindow(
                root,
                android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
                true);
        popup.setBackgroundDrawable(new android.graphics.drawable.ColorDrawable(
                android.graphics.Color.TRANSPARENT));
        popup.setElevation(16f);
        popup.setOutsideTouchable(false);

        android.view.View rootView = getWindow().getDecorView().getRootView();
        popup.showAtLocation(rootView, android.view.Gravity.CENTER, 0, 0);

        // ── Status bar height — drag offset fix
        int statusBarH = 0;
        int resId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resId > 0) statusBarH = getResources().getDimensionPixelSize(resId);
        final int statusBarHeight = statusBarH;

        // ── Drag — proper position tracking
        final int[] popXY = {0, 0};
        final int[] lastXY = {0, 0};
        final boolean[] isDragging = {false};

        dragHandle.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case android.view.MotionEvent.ACTION_DOWN:
                    isDragging[0] = true;
                    lastXY[0] = (int) event.getRawX();
                    lastXY[1] = (int) event.getRawY();
                    int[] loc2 = new int[2];
                    root.getLocationOnScreen(loc2);
                    popXY[0] = loc2[0];
                    popXY[1] = loc2[1] - statusBarHeight;
                    break;
                case android.view.MotionEvent.ACTION_MOVE:
                    if (!isDragging[0]) break;
                    int dx = (int) event.getRawX() - lastXY[0];
                    int dy = (int) event.getRawY() - lastXY[1];
                    popXY[0] += dx;
                    popXY[1] += dy;
                    popup.update(popXY[0], popXY[1], -1, -1);
                    lastXY[0] = (int) event.getRawX();
                    lastXY[1] = (int) event.getRawY();
                    break;
                case android.view.MotionEvent.ACTION_UP:
                case android.view.MotionEvent.ACTION_CANCEL:
                    isDragging[0] = false;
                    break;
            }
            return true;
        });

        btnCloseX.setOnClickListener(v -> {
            // Cancel — restore original
            targetText.setStrokeColor(strokeC[0]);
            targetText.setStrokeWidth(strokeW[0]);
            targetText.invalidate();
            popup.dismiss();
        });

        btnCancel.setOnClickListener(v -> popup.dismiss());

        btnRemove.setOnClickListener(v -> {
            targetText.setStrokeWidth(0f);
            targetText.invalidate();
            targetText.requestLayout();
            saveCurrentPage();
            exportToJson();
            popup.dismiss();
        });

        btnApply.setOnClickListener(v -> {
            targetText.setTextGradient(null);
            targetText.setTag(R.id.tv_move_speed, null);
            targetText.getPaint().setShader(null);
            targetText.setStrokeColor(strokeC[0]);
            targetText.setStrokeWidth(strokeW[0]);
            targetText.invalidate();
            targetText.requestLayout();
            saveCurrentPage();
            exportToJson();
            Toast.makeText(this, "✅ Stroke Apply", Toast.LENGTH_SHORT).show();
            popup.dismiss();
        });
    }


    private void loadGroupsFromJson(JSONArray groupsArray) {
        allGroups.clear();

        try {
            for (int g = 0; g < groupsArray.length(); g++) {
                JSONArray groupArr = groupsArray.getJSONArray(g);
                List<View> group = new ArrayList<>();

                float sw = mainLayout.getWidth();
                float sh = mainLayout.getHeight();

                for (int v = 0; v < groupArr.length(); v++) {
                    JSONObject vObj = groupArr.getJSONObject(v);
                    String type = vObj.optString("type", "");

                    float tx = (float) (vObj.getDouble("xPercent") * sw / 100);
                    float ty = (float) (vObj.getDouble("yPercent") * sh / 100);

                    // ── Layout ના views match
                    for (int c = 0; c < mainLayout.getChildCount(); c++) {
                        View child = mainLayout.getChildAt(c);
                        if (child == main_image_view) continue;

                        float cx = child.getX();
                        float cy = child.getY();

                        // ── Position match (5px tolerance)
                        if (Math.abs(cx - tx) < 5 && Math.abs(cy - ty) < 5) {

                            boolean typeMatch = false;
                            if (type.equals("text") && child instanceof StrokeTextView) {
                                typeMatch = true;
                            } else if (type.equals("sticker") && child instanceof ImageView) {
                                typeMatch = true;
                            }

                            if (typeMatch && !isViewInAnyGroup(child)) {
                                group.add(child);
                                break;
                            }
                        }
                    }
                }

                if (group.size() >= 2) {
                    allGroups.add(group);
                    int groupIdx = allGroups.size() - 1;
                    for (View gv : group) {
                        gv.setTag(R.id.seek_move_speed, "GROUP_" + groupIdx);
                    }
                    applyGroupBorder(group, groupIdx);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isViewInAnyGroup(View v) {
        for (List<View> g : allGroups) {
            if (g.contains(v)) return true;
        }
        return false;
    }


    private void showBorderStyleDialog(final TextView targetText) {

        String[] borderStyles = {"No Border",        // 0
                "Simple Black",     // 1
                "Dashed",           // 2
                "Dotted",           // 3
                "Thick Red",        // 4
                "Thick Blue",       // 5
                "Thick Green",      // 6
                "Double Border",    // 7
                "Rounded Simple",   // 8
                "Rounded Thick"     // 9
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Border Style પસંદ કરો");

        // Custom List Adapter - Preview સાથે
        android.widget.ArrayAdapter<String> adapter = new android.widget.ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, borderStyles) {
            @Override
            public View getView(int position, View convertView, android.view.ViewGroup parent) {
                // દરેક item માં preview TextView બતાવો
                LinearLayout row = new LinearLayout(getContext());
                row.setOrientation(LinearLayout.HORIZONTAL);
                row.setPadding(20, 15, 20, 15);
                row.setGravity(Gravity.CENTER_VERTICAL);

                // Preview Box
                TextView preview = new TextView(getContext());
                preview.setText("  Aa  ");
                preview.setTextSize(14);
                preview.setTextColor(Color.BLACK);
                preview.setPadding(16, 8, 16, 8);

                // Border Preview Apply
                GradientDrawable previewGd = new GradientDrawable();
                int bgColor = getStoredBackgroundColor(targetText);
                previewGd.setColor(bgColor == Color.TRANSPARENT ? Color.WHITE : bgColor);
                applyBorderStyle(previewGd, position);
                preview.setBackground(previewGd);

                // Style Name Label
                TextView label = new TextView(getContext());
                label.setText("  " + borderStyles[position]);
                label.setTextSize(16);
                label.setTextColor(Color.BLACK);

                row.addView(preview);
                row.addView(label);

                return row;
            }
        };

        builder.setAdapter(adapter, (dialog, which) -> {
            // User એ Select કરેલ Style Apply કરો
            GradientDrawable gd = new GradientDrawable();
            gd.setColor(getStoredBackgroundColor(targetText));
            applyBorderStyle(gd, which);
            targetText.setBackground(gd);

            // Border style tag માં store કરો (save/load માટે)
            targetText.setTag(R.id.btn_add_sticker, which);
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }


    private void applyBorderStyle(GradientDrawable gd, int styleIndex) {
        switch (styleIndex) {
            case 0: // No Border
                gd.setStroke(0, Color.TRANSPARENT);
                gd.setCornerRadius(0f);
                break;

            case 1: // Simple Black
                gd.setStroke(3, Color.BLACK);
                gd.setCornerRadius(0f);
                break;

            case 2: // Dashed
                gd.setStroke(3, Color.BLACK, 15f, 8f);
                gd.setCornerRadius(0f);
                break;

            case 3: // Dotted
                gd.setStroke(3, Color.BLACK, 4f, 6f);
                gd.setCornerRadius(0f);
                break;

            case 4: // Thick Red
                gd.setStroke(6, Color.RED);
                gd.setCornerRadius(0f);
                break;

            case 5: // Thick Blue
                gd.setStroke(6, Color.BLUE);
                gd.setCornerRadius(0f);
                break;

            case 6: // Thick Green
                gd.setStroke(6, Color.GREEN);
                gd.setCornerRadius(0f);
                break;

            case 7: // Double Border (Outer)
                gd.setStroke(10, Color.BLACK);
                gd.setColor(Color.WHITE); // inner white gap
                gd.setCornerRadius(0f);
                break;

            case 8: // Rounded Simple
                gd.setStroke(3, Color.BLACK);
                gd.setCornerRadius(20f);
                break;

            case 9: // Rounded Thick
                gd.setStroke(6, Color.DKGRAY);
                gd.setCornerRadius(30f);
                break;
        }
    }


    private String getGujaratiDate(int day, int month, int year) {
        String[] gujNums = {"૦", "૧", "૨", "૩", "૪", "૫", "૬", "૭", "૮", "૯"};

        String[] gujDays = {"એક", "બે", "ત્રણ", "ચાર", "પાંચ", "છ", "સાત", "આઠ", "નવ", "દસ", "અગિયાર", "બાર", "તેર", "ચૌદ", "પંદર", "સોળ", "સત્તર", "અઢાર", "ઓગણીસ", "વીસ", "એકવીસ", "બાવીસ", "તેવીસ", "ચોવીસ", "પચ્ચીસ", "છવ્વીસ", "સત્તાવીસ", "અઠ્ઠાવીસ", "ઓગણત્રીસ", "ત્રીસ", "એકત્રીસ"};

        String[] gujMonths = {"", "જાન્યુઆરી", "ફેબ્રુઆરી", "માર્ચ", "એપ્રિલ", "મે", "જૂન", "જુલાઈ", "ઓગસ્ટ", "સપ્ટેમ્બર", "ઓક્ટોબર", "નવેમ્બર", "ડિસેમ્બર"};

        // Gujarati numeral date

        // Weekday in Gujarati
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.set(year, month - 1, day);
        String[] weekDaysGuj = {"રવિવાર", "સોમવાર", "મંગળવાર", "બુધવાર", "ગુરુવાર", "શુક્રવાર", "શનિવાર"};
        String weekDay = weekDaysGuj[cal.get(java.util.Calendar.DAY_OF_WEEK) - 1];

        // Return base gujarati date — weekday attach karvano date picker ma
        return gujDays[day - 1] + " " + gujMonths[month] + ", " + toGujaratiNum(year, gujNums);
    }

    // Helper
    private String toGujaratiNum(int num, String[] gujNums) {
        String s = String.valueOf(num);
        StringBuilder sb = new StringBuilder();
        for (char c : s.toCharArray()) {
            sb.append(gujNums[c - '0']);
        }
        return sb.toString();
    }

    private void showEditTextDialog(final TextView targetText) {
        android.app.Dialog dialog = new android.app.Dialog(this, android.R.style.Theme_DeviceDefault_Light_NoActionBar_Fullscreen);
        dialog.setContentView(R.layout.dialog_edit_text);

        // ── Bind views
        TextView preview = dialog.findViewById(R.id.tv_edit_preview);
        android.widget.EditText input = dialog.findViewById(R.id.et_edit_input);
        Button btnApply = dialog.findViewById(R.id.btn_edit_apply);
        Button btnCancel = dialog.findViewById(R.id.btn_edit_cancel);
        TextView tvVoiceStatus = dialog.findViewById(R.id.tv_voice_status);

        // ── Tabs
        TextView tabTransform = dialog.findViewById(R.id.tab_transform);
        TextView tabDate = dialog.findViewById(R.id.tab_date);
        TextView tabVoice = dialog.findViewById(R.id.tab_voice);
        TextView tabClip = dialog.findViewById(R.id.tab_clip);

        // ── Panels
        LinearLayout panelTransform = dialog.findViewById(R.id.panel_transform);
        LinearLayout panelDate = dialog.findViewById(R.id.panel_date);
        LinearLayout panelVoice = dialog.findViewById(R.id.panel_voice);
        LinearLayout panelClip = dialog.findViewById(R.id.panel_clip);

        // ── Set text
        input.setText(targetText.getText().toString());
        input.setSelection(input.getText().length());
        preview.setText(targetText.getText().toString());
        preview.setTextColor(targetText.getCurrentTextColor());

        // ── Live preview
        input.addTextChangedListener(new android.text.TextWatcher() {
            public void beforeTextChanged(CharSequence s, int st, int c, int a) {
            }

            public void onTextChanged(CharSequence s, int st, int b, int c) {
                preview.setText(s.toString());
            }

            public void afterTextChanged(android.text.Editable s) {
            }
        });

        // ── Tab switch
        TextView[] tabs = {tabTransform, tabDate, tabVoice, tabClip};
        LinearLayout[] panels = {panelTransform, panelDate, panelVoice, panelClip};

        Runnable switchTab = () -> {
        };
        final int[] activeTab = {0};

        for (int i = 0; i < tabs.length; i++) {
            final int idx = i;
            tabs[i].setOnClickListener(v -> {
                activeTab[0] = idx;
                for (int j = 0; j < tabs.length; j++) {
                    boolean sel = j == idx;
                    tabs[j].setBackgroundResource(sel ? R.drawable.bg_tab_selected : R.drawable.bg_tab_normal);
                    tabs[j].setTextColor(sel ? Color.WHITE : Color.parseColor("#1A237E"));
                    panels[j].setVisibility(sel ? View.VISIBLE : View.GONE);
                }
            });
        }

        // Default tab
        tabs[0].callOnClick();

        // ── Transform buttons
        LinearLayout rowTransform = dialog.findViewById(R.id.row_transform);
        String[][] transforms = {{"UPPER", "ABC ↑"}, {"LOWER", "abc ↓"}, {"TITLE", "Abc"}, {"CLEAR", "✕ Clear"}};
        int[] transBg = {Color.parseColor("#E3F2FD"), Color.parseColor("#E8F5E9"), Color.parseColor("#FFF8E1"), Color.parseColor("#FFEBEE")};
        int[] transText = {Color.parseColor("#0D47A1"), Color.parseColor("#1B5E20"), Color.parseColor("#E65100"), Color.parseColor("#B71C1C")};

        for (int i = 0; i < transforms.length; i++) {
            final String tKey = transforms[i][0];
            Button tb = new Button(this);
            tb.setText(transforms[i][1]);
            tb.setTextSize(12);
            tb.setTextColor(transText[i]);
            LinearLayout.LayoutParams tp = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
            tp.setMargins(0, 0, 6, 0);
            tb.setLayoutParams(tp);
            tb.setBackgroundColor(transBg[i]);
            tb.setOnClickListener(v -> {
                String cur = input.getText().toString();
                switch (tKey) {
                    case "UPPER":
                        input.setText(cur.toUpperCase());
                        break;
                    case "LOWER":
                        input.setText(cur.toLowerCase());
                        break;
                    case "TITLE":
                        StringBuilder sb = new StringBuilder();
                        boolean nu = true;
                        for (char c : cur.toCharArray()) {
                            if (Character.isWhitespace(c)) {
                                nu = true;
                                sb.append(c);
                            } else if (nu) {
                                sb.append(Character.toUpperCase(c));
                                nu = false;
                            } else {
                                sb.append(Character.toLowerCase(c));
                            }
                        }
                        input.setText(sb.toString());
                        break;
                    case "CLEAR":
                        input.setText("");
                        break;
                }
                input.setSelection(input.getText().length());
            });
            rowTransform.addView(tb);
        }

        // ── Date & Time
        Button btnDatePick = dialog.findViewById(R.id.btn_date_pick);
        Button btnTimePick = dialog.findViewById(R.id.btn_time_pick);
        setupDatePanel(btnDatePick, btnTimePick, input);

        // ── Voice
        Button btnVoice = dialog.findViewById(R.id.btn_voice_input);
        btnVoice.setOnClickListener(v -> {
            android.content.Intent voiceIntent = new android.content.Intent(android.speech.RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            voiceIntent.putExtra(android.speech.RecognizerIntent.EXTRA_LANGUAGE_MODEL, android.speech.RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            voiceIntent.putExtra(android.speech.RecognizerIntent.EXTRA_LANGUAGE, "gu-IN");
            voiceIntent.putExtra(android.speech.RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "gu-IN");
            voiceIntent.putExtra(android.speech.RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE, false);
            voiceIntent.putExtra(android.speech.RecognizerIntent.EXTRA_PROMPT, "ગુજરાતીમાં બોલો...");
            try {
                tvVoiceStatus.setText("🎤 સાંભળી રહ્યો છે...");
                voiceTargetInput = input;
                startActivityForResult(voiceIntent, REQUEST_VOICE_INPUT);
            } catch (Exception e) {
                tvVoiceStatus.setText("❌ Voice support નથી");
                Toast.makeText(this, "Voice input support નથી", Toast.LENGTH_SHORT).show();
            }
        });

        // ── Clipboard
        Button btnCopy = dialog.findViewById(R.id.btn_clip_copy);
        Button btnPaste = dialog.findViewById(R.id.btn_clip_paste);

        btnCopy.setOnClickListener(v -> {
            android.content.ClipboardManager cb = (android.content.ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            cb.setPrimaryClip(android.content.ClipData.newPlainText("text", input.getText().toString()));
            Toast.makeText(this, "✅ Copy થઈ ગયું!", Toast.LENGTH_SHORT).show();
        });

        btnPaste.setOnClickListener(v -> {
            android.content.ClipboardManager cb = (android.content.ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            if (cb.hasPrimaryClip() && cb.getPrimaryClip().getItemCount() > 0) {
                CharSequence pt = cb.getPrimaryClip().getItemAt(0).getText();
                if (pt != null) {
                    int cursor = input.getSelectionStart();
                    input.getText().insert(cursor, pt);
                    Toast.makeText(this, "✅ Paste થઈ ગયું!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Clipboard ખાલી છે", Toast.LENGTH_SHORT).show();
            }
        });

        // ── Apply / Cancel
        btnApply.setOnClickListener(v -> {
            targetText.setText(input.getText().toString());
            dialog.dismiss();
        });
        btnCancel.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void setupDatePanel(Button btnDate, Button btnTime, android.widget.EditText input) {
        btnDate.setOnClickListener(v -> {
            java.util.Calendar cal = java.util.Calendar.getInstance();
            new android.app.DatePickerDialog(this, (dp, year, month, day) -> showDateFormatDialog(day, month, year, input), cal.get(java.util.Calendar.YEAR), cal.get(java.util.Calendar.MONTH), cal.get(java.util.Calendar.DAY_OF_MONTH)).show();
        });

        btnTime.setOnClickListener(v -> {
            java.util.Calendar cal = java.util.Calendar.getInstance();
            new android.app.TimePickerDialog(this, (tp, hour, minute) -> {
                String ampm = hour < 12 ? "સવારે" : (hour < 17 ? "બપોરે" : (hour < 20 ? "સાંજે" : "રાત્રે"));
                int h12 = hour % 12;
                if (h12 == 0) h12 = 12;
                String te = String.format("%02d:%02d %s", h12, minute, hour < 12 ? "AM" : "PM");
                String tg = ampm + " " + h12 + ":" + String.format("%02d", minute) + " વાગ્યે";
                new AlertDialog.Builder(this).setTitle("Time Format").setItems(new String[]{te, tg, te + " (" + tg + ")"}, (d, w) -> {
                    String[] opts = {te, tg, te + " (" + tg + ")"};
                    int c = input.getSelectionStart();
                    input.getText().insert(c, opts[w] + " ");
                }).show();
            }, cal.get(java.util.Calendar.HOUR_OF_DAY), cal.get(java.util.Calendar.MINUTE), false).show();
        });
    }

    private void showDateFormatDialog(int day, int month, int year, android.widget.EditText input) {
        // Same date format dialog as before
        String[] engMonths = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
        String[] engMonthsShort = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        String[] gujNums = {"૦", "૧", "૨", "૩", "૪", "૫", "૬", "૭", "૮", "૯"};
        String[] weekDaysGuj = {"રવિવાર", "સોમવાર", "મંગળવાર", "બુધવાર", "ગુરુવાર", "શુક્રવાર", "શનિવાર"};
        String[] weekDays = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};

        java.util.Calendar sel = java.util.Calendar.getInstance();
        sel.set(year, month, day);
        int dow = sel.get(java.util.Calendar.DAY_OF_WEEK) - 1;

        String suffix = "th";
        if (day == 1 || day == 21 || day == 31) suffix = "st";
        else if (day == 2 || day == 22) suffix = "nd";
        else if (day == 3 || day == 23) suffix = "rd";

        String gD = toGujaratiNum(day, gujNums);
        String gM = toGujaratiNum(month + 1, gujNums);
        String gY = toGujaratiNum(year, gujNums);
        String gujDate = getGujaratiDate(day, month + 1, year);
        String wdGuj = weekDaysGuj[dow];
        String wd = weekDays[dow];

        String[] options = {day + " " + engMonths[month] + " " + year, engMonthsShort[month] + " " + day + ", " + year, String.format("%02d/%02d/%04d", day, month + 1, year), String.format("%02d-%02d-%04d", day, month + 1, year), day + suffix + " " + engMonths[month] + " " + year, wd + ", " + day + " " + engMonths[month] + " " + year, gD + "/" + gM + "/" + gY, gD + "/" + gM + "/" + gY + " ને " + wdGuj, "તા. " + gD + "/" + gM + "/" + gY, "તા. " + gD + "/" + gM + "/" + gY + " ને " + wdGuj, wdGuj + " તા. " + gD + "/" + gM + "/" + gY, gujDate, gujDate + " ને " + wdGuj, day + " " + engMonths[month] + " " + year + "\n(" + gD + "/" + gM + "/" + gY + " ને " + wdGuj + ")", String.format("%02d.%02d.%04d", day, month + 1, year),};

        new AlertDialog.Builder(this).setTitle("Date Format પસંદ કરો").setItems(options, (d, w) -> {
            int c = input.getSelectionStart();
            input.getText().insert(c, options[w] + " ");
        }).show();
    }


    private void selectTextView(StrokeTextView textView) {
        if (isMultiSelectMode) {
            // ── Multi-select mode — selection toggle only, NO controls popup
            if (selectedViews.contains(textView)) {
                selectedViews.remove(textView);
                restoreViewBorder(textView);
                selectedOriginalSizes.clear();
                for (View s : selectedViews) saveOriginalSize(s);
            } else {
                selectedViews.add(textView);
                applySelectionBorder(textView);
                saveOriginalSize(textView);
            }
            if (seekMultiSize != null) seekMultiSize.setProgress(50);
            if (tvSizeLabel != null) tvSizeLabel.setText("+0.0sp");
            updateMultiSelectBtnLabel();
            return; // ✅ Multi-select mode = controls popup show નહીં
        }

        // ── Single select mode
        if (currentlySelectedTextView != null && currentlySelectedTextView != textView) {
            restoreTextBorder(currentlySelectedTextView);
        }

        if (currentlySelectedView != null && currentlySelectedView != textView && !(currentlySelectedView instanceof TextView)) {
            currentlySelectedView.setBackground(null);
            currentlySelectedView.setPadding(0, 0, 0, 0);
        }

        // ✅ showSelectionControls() REMOVE — tap પર call નહીં
        // controls ACTION_UP માં show થશે

        currentlySelectedTextView = textView;
        currentlySelectedView = textView;

        // Selection border
        GradientDrawable gd = new GradientDrawable();
        gd.setColor(getStoredBackgroundColor(textView));
        gd.setStroke(3, Color.MAGENTA);
        gd.setCornerRadius(8f);
        textView.setBackground(gd);
    }


    private void selectView(View view) {
        // Single select — controls show
        // showSelectionControls(view);

        // ── Multi-select mode
        // selectView() ની multi-select block માં:
        if (isMultiSelectMode) {
            if (selectedViews.contains(view)) {
                selectedViews.remove(view);
                restoreViewBorder(view);
                // Rebuild original sizes
                selectedOriginalSizes.clear();
                for (View s : selectedViews) saveOriginalSize(s);
            } else {
                selectedViews.add(view);
                applySelectionBorder(view);
                saveOriginalSize(view);
            }
            // Seekbar center reset
            seekMultiSize.setProgress(50);
            tvSizeLabel.setText("+0.0sp");
            updateMultiSelectBtnLabel();
            return; // ← અહીં return — single select logic skip
        }

        // ── Single select mode
        deselectAll();
        currentlySelectedView = view;

        GradientDrawable gd = new GradientDrawable();
        gd.setStroke(5, Color.MAGENTA);
        view.setPadding(8, 8, 8, 8);
        view.setBackground(gd);

    }

    // બધા views deselect
    private void deselectAll() {
        // Text deselect
        if (selectionControlsPopup != null && selectionControlsPopup.isShowing()) {
            selectionControlsPopup.dismiss();
        }

        if (currentlySelectedTextView != null) {
            restoreTextBorder(currentlySelectedTextView);
            currentlySelectedTextView = null;
        }

        // Image/Sticker deselect
        if (currentlySelectedView != null && !(currentlySelectedView instanceof TextView)) {
            currentlySelectedView.setBackground(null);
            currentlySelectedView.setPadding(0, 0, 0, 0);
        }
        currentlySelectedView = null;

        // Multi-select list clear
        for (View v : selectedViews) {
            if (v instanceof StrokeTextView) {
                restoreTextBorder((StrokeTextView) v);
            } else {
                v.setBackground(null);
                v.setPadding(0, 0, 0, 0);
            }
        }
        selectedViews.clear();
    }


    private void restoreTextBorder(StrokeTextView tv) {
        if (android.os.Build.VERSION.SDK_INT >=
                android.os.Build.VERSION_CODES.M) {
            tv.setForeground(null);
        }

        Object bgImageTag = tv.getTag(R.id.btn_sticker_gallery);
        String bgImageUri = bgImageTag != null ?
                bgImageTag.toString() : "";

        Object borderStyleTag = tv.getTag(R.id.btn_add_sticker);
        int borderStyle = borderStyleTag instanceof Integer ?
                (int) borderStyleTag : 0;

        int originalBg = getStoredBackgroundColor(tv);

        // ✅ User padding tag માંથી read કરો
        Object padTag = tv.getTag(R.id.btn_location);
        int userPadX = 20; // default
        int userPadY = 20; // default
        if (padTag instanceof int[]) {
            int[] userPad = (int[]) padTag;
            userPadX = userPad[0];
            userPadY = userPad.length > 1 ? userPad[1] : userPad[0];
        }
        int strokeExtra = (int) Math.ceil(tv.getStrokeWidth()) + 4;
        int finalPadX = strokeExtra + userPadX;
        int finalPadY = strokeExtra + userPadY;

        // ── BG Image હોય તો
        if (!bgImageUri.isEmpty()) {
            Drawable currentBg = tv.getBackground();
            if (currentBg != null &&
                    !(currentBg instanceof GradientDrawable)) {
                tv.setBackground(currentBg);
                // ✅ User padding restore
                tv.setPadding(finalPadX, finalPadY,
                        finalPadX, finalPadY);
                return;
            }
            Uri uri = Uri.parse(bgImageUri);
            applyTextBgImage(uri, tv);
            tv.setPadding(finalPadX, finalPadY,
                    finalPadX, finalPadY);
            return;
        }

        // ── Normal color border
        GradientDrawable gd = new GradientDrawable();
        gd.setColor(originalBg);

        // ✅ tv_border_info tag mathi saved width/color/corner/style read karo
        Object borderInfoTag = tv.getTag(R.id.tv_border_info);
        if (borderInfoTag instanceof int[]) {
            int[] info = (int[]) borderInfoTag;
            int savedWidth  = info.length > 0 ? info[0] : 2;
            int savedColor  = info.length > 1 ? info[1] : Color.BLACK;
            int savedCorner = info.length > 2 ? info[2] : 0;
            int savedStyle  = info.length > 3 ? info[3] : borderStyle;
            gd.setCornerRadius(dpToPx(savedCorner));
            switch (savedStyle) {
                case 0: // Solid
                    gd.setStroke(dpToPx(savedWidth), savedColor);
                    break;
                case 1: // Dash
                    gd.setStroke(dpToPx(savedWidth), savedColor, dpToPx(8), dpToPx(4));
                    break;
                case 2: // Dot
                    gd.setStroke(dpToPx(savedWidth), savedColor, dpToPx(2), dpToPx(4));
                    break;
                case 3: // Double
                    gd.setStroke(dpToPx(savedWidth + 2), savedColor);
                    break;
                default:
                    applyBorderStyle(gd, savedStyle);
            }
        } else {
            applyBorderStyle(gd, borderStyle);
        }
        tv.setBackground(gd);

        // ✅ User padding restore
        tv.setPadding(finalPadX, finalPadY, finalPadX, finalPadY);
    }


    // Text ની original border restore
/*    private void restoreTextBorder(StrokeTextView tv) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            tv.setForeground(null);
        }
        Object bgImageTag = tv.getTag(R.id.btn_sticker_gallery);
        String bgImageUri = bgImageTag != null ? bgImageTag.toString() : "";

        Object borderStyleTag = tv.getTag(R.id.btn_add_sticker);
        int borderStyle = borderStyleTag instanceof Integer ? (int) borderStyleTag : 0;

        int originalBg = getStoredBackgroundColor(tv);

        // ✅ જો text background image set હોય તો તેને remove ન કરવી
        if (!bgImageUri.isEmpty()) {
            Drawable currentBg = tv.getBackground();

            // જો current background પહેલેથી BitmapDrawable/LayerDrawable હોય તો same રાખો
            if (currentBg != null && !(currentBg instanceof GradientDrawable)) {
                tv.setBackground(currentBg);
                tv.setPadding(20, 20, 20, 20);
                return;
            }

            // fallback: tag માંથી image ફરી load કરો
            Uri uri = Uri.parse(bgImageUri);
            applyTextBgImage(uri, tv);
            tv.setPadding(20, 20, 20, 20);
            return;
        }

        // ✅ image ન હોય ત્યારે normal color/border restore
        GradientDrawable gd = new GradientDrawable();
        gd.setColor(originalBg);
        applyBorderStyle(gd, borderStyle);

        tv.setBackground(gd);
        tv.setPadding(20, 20, 20, 20);
    }*/

    // Generic view ની border restore
    private void restoreViewBorder(View v) {
        if (v instanceof StrokeTextView) {
            restoreTextBackgroundSafe((StrokeTextView) v);
            return;
        }

        if (v instanceof ImageView) {
            v.setBackground(null);
            v.setPadding(0, 0, 0, 0);
        }
    }

    private void restoreTextBackgroundSafe(final StrokeTextView tv) {
        if (tv == null) return;

        if (android.os.Build.VERSION.SDK_INT >=
                android.os.Build.VERSION_CODES.M) {
            tv.setForeground(null);
        }

        // ✅ User padding tag માંથી read
        Object padTag = tv.getTag(R.id.btn_location);
        int userPadX = 20;
        int userPadY = 20;
        if (padTag instanceof int[]) {
            int[] userPad = (int[]) padTag;
            userPadX = userPad[0];
            userPadY = userPad.length > 1 ? userPad[1] : userPad[0];
        }
        int strokeExtra = (int) Math.ceil(tv.getStrokeWidth()) + 4;
        final int finalPadX = strokeExtra + userPadX;
        final int finalPadY = strokeExtra + userPadY;

        Object bgTag = tv.getTag(R.id.btn_sticker_gallery);
        String bgUri = bgTag != null ? bgTag.toString() : "";

        Object borderTag = tv.getTag(R.id.btn_add_sticker);
        int borderStyle = borderTag instanceof Integer ?
                (int) borderTag : 0;

        if (!bgUri.isEmpty()) {
            Glide.with(this).asBitmap()
                    .load(Uri.parse(bgUri))
                    .into(new CustomTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(
                                @NonNull Bitmap bitmap,
                                @Nullable Transition<? super Bitmap> t) {

                            int vw = tv.getWidth();
                            int vh = tv.getHeight();
                            if (vw <= 0) vw = 300;
                            if (vh <= 0) vh = 100;

                            Bitmap scaled = Bitmap.createScaledBitmap(
                                    bitmap, vw, vh, true);
                            android.graphics.drawable.BitmapDrawable bd =
                                    new android.graphics.drawable.BitmapDrawable(
                                            getResources(), scaled);

                            if (borderStyle > 0) {
                                GradientDrawable border =
                                        new GradientDrawable();
                                border.setColor(Color.TRANSPARENT);
                                applyBorderStyle(border, borderStyle);
                                android.graphics.drawable.LayerDrawable layer =
                                        new android.graphics.drawable.LayerDrawable(
                                                new Drawable[]{bd, border});
                                tv.setBackground(layer);
                            } else {
                                tv.setBackground(bd);
                            }

                            // ✅ User padding restore
                            tv.setPadding(finalPadX, finalPadY,
                                    finalPadX, finalPadY);
                        }

                        @Override
                        public void onLoadCleared(
                                @Nullable Drawable p) {
                        }
                    });
            return;
        }

        // ── Normal background
        int bgColor = getStoredBackgroundColor(tv);
        GradientDrawable gd = new GradientDrawable();
        gd.setColor(bgColor);

        // ✅ tv_border_info tag mathi width, color, corner read karo
        Object borderInfoTag = tv.getTag(R.id.tv_border_info);
        if (borderInfoTag instanceof int[]) {
            int[] info = (int[]) borderInfoTag;
            int savedWidth  = info.length > 0 ? info[0] : 2;
            int savedColor  = info.length > 1 ? info[1] : Color.BLACK;
            int savedCorner = info.length > 2 ? info[2] : 0;
            int savedStyle  = info.length > 3 ? info[3] : borderStyle;
            gd.setCornerRadius(dpToPx(savedCorner));
            switch (savedStyle) {
                case 0: // Solid
                    gd.setStroke(dpToPx(savedWidth), savedColor);
                    break;
                case 1: // Dash
                    gd.setStroke(dpToPx(savedWidth), savedColor, dpToPx(8), dpToPx(4));
                    break;
                case 2: // Dot
                    gd.setStroke(dpToPx(savedWidth), savedColor, dpToPx(2), dpToPx(4));
                    break;
                case 3: // Double
                    gd.setStroke(dpToPx(savedWidth + 2), savedColor);
                    break;
                default:
                    applyBorderStyle(gd, savedStyle);
            }
        } else {
            applyBorderStyle(gd, borderStyle);
        }
        tv.setBackground(gd);

        // ✅ User padding restore
        tv.setPadding(finalPadX, finalPadY, finalPadX, finalPadY);
    }


    private void showArcTextPopup(StrokeTextView arcView) {

        View popupView = LayoutInflater.from(this)
                .inflate(R.layout.popup_arc_text, null);

        arcPopupWindow = new PopupWindow(
                popupView,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                true
        );
        arcPopupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        arcPopupWindow.setElevation(12f);
        arcPopupWindow.setOutsideTouchable(false);

        View rootView = getWindow().getDecorView().getRootView();
        arcPopupWindow.showAtLocation(rootView, Gravity.CENTER, 0, 0);

        // ── Drag logic (same as before)
        View dragHandle = popupView.findViewById(R.id.drag_handle);
        final int[] lastX = {0};
        final int[] lastY = {0};
        dragHandle.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    lastX[0] = (int) event.getRawX();
                    lastY[0] = (int) event.getRawY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    int dx = (int) event.getRawX() - lastX[0];
                    int dy = (int) event.getRawY() - lastY[0];
                    int[] location = new int[2];
                    popupView.getLocationOnScreen(location);
                    arcPopupWindow.update(location[0] + dx, location[1] + dy, -1, -1);
                    lastX[0] = (int) event.getRawX();
                    lastY[0] = (int) event.getRawY();
                    break;
            }
            return true;
        });

        // ── Views
        EditText etText = popupView.findViewById(R.id.et_arc_text);
        SeekBar sbAngle = popupView.findViewById(R.id.sb_arc_angle);
        SeekBar sbRadius = popupView.findViewById(R.id.sb_arc_radius);
        TextView tvAngleVal = popupView.findViewById(R.id.tv_angle_value);
        TextView tvRadiusVal = popupView.findViewById(R.id.tv_radius_value);
        Button btnArcUp = popupView.findViewById(R.id.btn_arc_up);
        Button btnArcDown = popupView.findViewById(R.id.btn_arc_down);
        Button btnDone = popupView.findViewById(R.id.btn_arc_done);
        ImageView btnClose = popupView.findViewById(R.id.btn_close_popup);

        // ✅ KEY FIX: Arc mode ON karo — existing text par curve apply thay
        boolean wasArcMode = arcView.isArcMode();
        if (!wasArcMode) {
            arcView.setArcMode(true);
            arcView.setArcAngle(180f);
            arcView.setRadius(150f);
            arcView.setArcUp(true);
        }

        // ✅ Toggle button — arc ON/OFF
        Button btnToggleArc = popupView.findViewById(R.id.btn_toggle_arc);
        final boolean[] arcEnabled = {arcView.isArcMode()};

        // Toggle button UI update helper
        Runnable updateToggleBtn = () -> {
            if (btnToggleArc == null) return;
            if (arcEnabled[0]) {
                btnToggleArc.setText("⌢ Arc ON");
                btnToggleArc.setAlpha(1f);
            } else {
                btnToggleArc.setText("— Arc OFF");
                btnToggleArc.setAlpha(0.5f);
            }
        };
        updateToggleBtn.run();

        if (btnToggleArc != null) {
            btnToggleArc.setOnClickListener(v -> {
                arcEnabled[0] = !arcEnabled[0];
                arcView.setArcMode(arcEnabled[0]);
                updateToggleBtn.run();
            });
        }

        // ── Current values set (existing arc hoy to preserve karo)
        etText.setText(arcView.getText().toString());

        sbAngle.setMax(360);
        sbAngle.setProgress((int) arcView.getArcAngle());
        tvAngleVal.setText((int) arcView.getArcAngle() + "°");

        sbRadius.setMax(500);
        sbRadius.setProgress((int) arcView.getRadius());
        tvRadiusVal.setText(String.valueOf((int) arcView.getRadius()));

        // ── Arc Up/Down button highlight
        btnArcUp.setAlpha(arcView.isArcUp() ? 1f : 0.4f);
        btnArcDown.setAlpha(arcView.isArcUp() ? 0.4f : 1f);

        // ── Text change
        etText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                arcView.setText(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        // ── Angle
        sbAngle.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar sb, int p, boolean u) {
                float angle = Math.max(10, p);
                arcView.setArcAngle(angle);
                tvAngleVal.setText((int) angle + "°");
            }

            @Override
            public void onStartTrackingTouch(SeekBar sb) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar sb) {
                exportToJson();
            }
        });

        // ── Radius
        sbRadius.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar sb, int p, boolean u) {
                float rad = Math.max(50, p);
                arcView.setRadius(rad);
                tvRadiusVal.setText(String.valueOf((int) rad));
            }

            @Override
            public void onStartTrackingTouch(SeekBar sb) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar sb) {
                exportToJson();
            }
        });

        // ── Arc direction
        btnArcUp.setOnClickListener(v -> {
            arcView.setArcUp(true);
            btnArcUp.setAlpha(1f);
            btnArcDown.setAlpha(0.4f);
        });

        btnArcDown.setOnClickListener(v -> {
            arcView.setArcUp(false);
            btnArcUp.setAlpha(0.4f);
            btnArcDown.setAlpha(1f);
        });

        // ── Close — arc mode restore karo jો pehla nahi hatu
        btnClose.setOnClickListener(v -> {
            if (!wasArcMode) {
                arcView.setArcMode(false); // ✅ Cancel = original state restore
            }
            arcPopupWindow.dismiss();
        });

        // ── Done
        btnDone.setOnClickListener(v -> {
            exportToJson();
            arcPopupWindow.dismiss();
        });
    }


    private int dpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density + 0.5f);
    }

    private void applySelectionBorder(View v) {
        if (v instanceof StrokeTextView) {

            StrokeTextView tv = (StrokeTextView) v;

            if (hasTextBgImage(tv)) {

                // ✅ Background image ને touch ન કરવી
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    GradientDrawable border = new GradientDrawable();
                    border.setColor(Color.TRANSPARENT);
                    border.setStroke(6, Color.CYAN);
                    border.setCornerRadius(8f);
                    tv.setForeground(border);
                }

                tv.setPadding(20, 20, 20, 20);
                return;
            }

            GradientDrawable gd = new GradientDrawable();
            gd.setColor(getStoredBackgroundColor(tv));
            gd.setStroke(6, Color.CYAN);
            gd.setCornerRadius(8f);

            tv.setBackground(gd);
            tv.setPadding(20, 20, 20, 20);
            return;
        }

        if (v instanceof ImageView) {
            GradientDrawable gd = new GradientDrawable();
            gd.setColor(Color.TRANSPARENT);
            gd.setStroke(6, Color.CYAN);
            gd.setCornerRadius(8f);

            v.setBackground(gd);
            v.setPadding(8, 8, 8, 8);
        }
    }

    // Multi-select button label update
    private void updateMultiSelectBtnLabel() {

        if (btnMultiSelect == null) return;

        // ── Selected count TextView update
        View includeView = findViewById(R.id.include_multi_select_toolbar);
        if (includeView != null) {
            TextView tvCount = includeView.findViewById(R.id.tv_selected_count);
            if (tvCount != null) {
                tvCount.setText(selectedViews.size() + " selected");
            }
        }

        // ── btnMultiSelect label
        TextView tvLabel = (TextView) btnMultiSelect.getChildAt(1);
        if (!isMultiSelectMode) {
            if (tvLabel != null) tvLabel.setText("Multi");
            btnMultiSelect.setBackgroundResource(R.drawable.bg_multiselect_btn);
            return;
        }

        if (selectedViews.isEmpty()) {
            if (tvLabel != null) tvLabel.setText("Multi: ON");
            btnMultiSelect.setBackgroundColor(Color.parseColor("#FFF9C4"));
        } else {
            if (tvLabel != null) tvLabel.setText("(" + selectedViews.size() + ")");
            btnMultiSelect.setBackgroundColor(Color.parseColor("#BBDEFB"));
        }
    }

    private void deselectCurrentText() {
        if (currentlySelectedTextView != null) {
            restoreTextBorder(currentlySelectedTextView);
            currentlySelectedTextView = null;
        }
        // Image view પણ deselect
        if (currentlySelectedView != null && !(currentlySelectedView instanceof TextView)) {
            currentlySelectedView.setBackground(null);
            currentlySelectedView.setPadding(0, 0, 0, 0);
            currentlySelectedView = null;
        }
    }
    // ══════════════════════════════════════════════════════════
    // Color Picker Popup — Movable, Horizontal Color Scroll
    // ══════════════════════════════════════════════════════════
    private void showColorPickerPopup(int initialColor, java.util.function.Consumer<Integer> onColorSelected) {

        final int[] selectedColor = {initialColor};

        // ── Root layout
        android.widget.LinearLayout root = new android.widget.LinearLayout(this);
        root.setOrientation(android.widget.LinearLayout.VERTICAL);
        root.setLayoutParams(new android.view.ViewGroup.LayoutParams(
                dpToPx(300), android.view.ViewGroup.LayoutParams.WRAP_CONTENT));

        // ── Drag Handle
        android.widget.LinearLayout dragHandle = new android.widget.LinearLayout(this);
        dragHandle.setOrientation(android.widget.LinearLayout.HORIZONTAL);
        dragHandle.setBackgroundColor(0xFF2A3439);
        dragHandle.setGravity(android.view.Gravity.CENTER_VERTICAL);
        dragHandle.setPadding(dpToPx(10), 0, dpToPx(6), 0);
        dragHandle.setLayoutParams(new android.widget.LinearLayout.LayoutParams(
                android.widget.LinearLayout.LayoutParams.MATCH_PARENT, dpToPx(32)));

        android.widget.TextView dragDots = new android.widget.TextView(this);
        dragDots.setText("⠿");
        dragDots.setTextColor(0xFF9CA3AF);
        dragDots.setTextSize(12);
        dragDots.setGravity(android.view.Gravity.CENTER);
        dragDots.setLayoutParams(new android.widget.LinearLayout.LayoutParams(
                dpToPx(16), android.widget.LinearLayout.LayoutParams.MATCH_PARENT));

        android.widget.TextView dragTitle = new android.widget.TextView(this);
        dragTitle.setText("🎨 Color");
        dragTitle.setTextColor(0xFFF3F4F6);
        dragTitle.setTextSize(9);
        dragTitle.setTypeface(null, android.graphics.Typeface.BOLD);
        android.widget.LinearLayout.LayoutParams titleLp =
                new android.widget.LinearLayout.LayoutParams(0,
                        android.widget.LinearLayout.LayoutParams.MATCH_PARENT, 1f);
        titleLp.setMarginStart(dpToPx(4));
        dragTitle.setLayoutParams(titleLp);
        dragTitle.setGravity(android.view.Gravity.CENTER_VERTICAL);

        android.widget.TextView btnClose = new android.widget.TextView(this);
        btnClose.setText("✕");
        btnClose.setTextColor(0xFF9CA3AF);
        btnClose.setTextSize(12);
        btnClose.setGravity(android.view.Gravity.CENTER);
        btnClose.setLayoutParams(new android.widget.LinearLayout.LayoutParams(dpToPx(28), dpToPx(22)));
        btnClose.setBackgroundColor(0xFF374151);

        dragHandle.addView(dragDots);
        dragHandle.addView(dragTitle);
        dragHandle.addView(btnClose);
        root.addView(dragHandle);

        // ── Content
        android.widget.LinearLayout content = new android.widget.LinearLayout(this);
        content.setOrientation(android.widget.LinearLayout.VERTICAL);
        content.setBackgroundColor(0xFF455A64);
        content.setPadding(dpToPx(10), dpToPx(10), dpToPx(10), dpToPx(10));
        root.addView(content);

        // ── Color preview
        android.graphics.drawable.GradientDrawable previewBg = new android.graphics.drawable.GradientDrawable();
        previewBg.setColor(initialColor);
        previewBg.setCornerRadius(dpToPx(6));
        previewBg.setStroke(dpToPx(1), 0xFFD1D5DB);
        android.view.View colorPreview = new android.view.View(this);
        android.widget.LinearLayout.LayoutParams previewLp =
                new android.widget.LinearLayout.LayoutParams(
                        android.widget.LinearLayout.LayoutParams.MATCH_PARENT, dpToPx(36));
        previewLp.setMargins(0, 0, 0, dpToPx(8));
        colorPreview.setLayoutParams(previewLp);
        colorPreview.setBackground(previewBg);
        content.addView(colorPreview);

        // ── Horizontal color scroll
        final int[][] colorRows = {
            // Row 1 - Basic
            {0xFFFF0000, 0xFFFF4500, 0xFFFF8C00, 0xFFFFD700, 0xFFFFFF00,
             0xFFADFF2F, 0xFF32CD32, 0xFF00FA9A, 0xFF00FFFF, 0xFF00BFFF,
             0xFF1E90FF, 0xFF0000FF, 0xFF8A2BE2, 0xFFFF00FF, 0xFFFF1493},
            // Row 2 - Light
            {0xFFFFFFFF, 0xFFF5F5F5, 0xFFE0E0E0, 0xFFC0C0C0, 0xFF9E9E9E,
             0xFF757575, 0xFF616161, 0xFF424242, 0xFF212121, 0xFF000000,
             0xFFFFCDD2, 0xFFF8BBD0, 0xFFE1BEE7, 0xFFD1C4E9, 0xFFC5CAE9},
            // Row 3 - Dark/Material
            {0xFFB71C1C, 0xFF880E4F, 0xFF4A148C, 0xFF1A237E, 0xFF0D47A1,
             0xFF006064, 0xFF1B5E20, 0xFF33691E, 0xFFF57F17, 0xFFE65100,
             0xFF3E2723, 0xFF263238, 0xFF37474F, 0xFF546E7A, 0xFF78909C},
        };

        String[] rowLabels = {"🌈 Basic", "⬜ Light/Dark", "🎨 Material"};

        for (int r = 0; r < colorRows.length; r++) {
            // Row label
            android.widget.TextView rowLabel = new android.widget.TextView(this);
            rowLabel.setText(rowLabels[r]);
            rowLabel.setTextColor(0xFFCFD8DC);
            rowLabel.setTextSize(10);
            rowLabel.setTypeface(null, android.graphics.Typeface.BOLD);
            android.widget.LinearLayout.LayoutParams lblLp =
                    new android.widget.LinearLayout.LayoutParams(
                            android.widget.LinearLayout.LayoutParams.WRAP_CONTENT,
                            android.widget.LinearLayout.LayoutParams.WRAP_CONTENT);
            lblLp.setMargins(0, dpToPx(4), 0, dpToPx(2));
            rowLabel.setLayoutParams(lblLp);
            content.addView(rowLabel);

            // Horizontal ScrollView
            android.widget.HorizontalScrollView hsv = new android.widget.HorizontalScrollView(this);
            hsv.setHorizontalScrollBarEnabled(false);
            android.widget.LinearLayout.LayoutParams hsvLp =
                    new android.widget.LinearLayout.LayoutParams(
                            android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
                            android.widget.LinearLayout.LayoutParams.WRAP_CONTENT);
            hsvLp.setMargins(0, 0, 0, dpToPx(2));
            hsv.setLayoutParams(hsvLp);

            android.widget.LinearLayout row = new android.widget.LinearLayout(this);
            row.setOrientation(android.widget.LinearLayout.HORIZONTAL);
            row.setPadding(0, dpToPx(2), 0, dpToPx(2));

            for (int c : colorRows[r]) {
                final int fc = c;
                android.view.View btn = new android.view.View(this);
                android.widget.LinearLayout.LayoutParams btnLp =
                        new android.widget.LinearLayout.LayoutParams(dpToPx(36), dpToPx(36));
                btnLp.setMargins(dpToPx(2), 0, dpToPx(2), 0);
                btn.setLayoutParams(btnLp);

                android.graphics.drawable.GradientDrawable btnBg =
                        new android.graphics.drawable.GradientDrawable();
                btnBg.setColor(fc);
                btnBg.setCornerRadius(dpToPx(4));
                btnBg.setStroke(dpToPx(1), 0xFFD1D5DB);
                btn.setBackground(btnBg);

                btn.setOnClickListener(v -> {
                    selectedColor[0] = fc;
                    previewBg.setColor(fc);
                    colorPreview.setBackground(previewBg);
                    onColorSelected.accept(fc); // real-time apply
                });
                row.addView(btn);
            }
            hsv.addView(row);
            content.addView(hsv);
        }

        // ── HEX input row
        android.widget.LinearLayout hexRow = new android.widget.LinearLayout(this);
        hexRow.setOrientation(android.widget.LinearLayout.HORIZONTAL);
        hexRow.setGravity(android.view.Gravity.CENTER_VERTICAL);
        android.widget.LinearLayout.LayoutParams hexRowLp =
                new android.widget.LinearLayout.LayoutParams(
                        android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
                        android.widget.LinearLayout.LayoutParams.WRAP_CONTENT);
        hexRowLp.setMargins(0, dpToPx(8), 0, dpToPx(8));
        hexRow.setLayoutParams(hexRowLp);

        android.widget.TextView hexLabel = new android.widget.TextView(this);
        hexLabel.setText("HEX");
        hexLabel.setTextColor(0xFFCFD8DC);
        hexLabel.setTextSize(11);
        hexLabel.setTypeface(null, android.graphics.Typeface.BOLD);
        hexLabel.setPadding(0, 0, dpToPx(8), 0);

        android.widget.EditText etHex = new android.widget.EditText(this);
        etHex.setText(String.format("#%06X", (0xFFFFFF & initialColor)));
        etHex.setTextColor(0xFF111827);
        etHex.setTextSize(12);
        etHex.setBackgroundColor(0xFF546E7A);
        etHex.setPadding(dpToPx(8), dpToPx(6), dpToPx(8), dpToPx(6));
        etHex.setLayoutParams(new android.widget.LinearLayout.LayoutParams(0,
                android.widget.LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

        hexRow.addView(hexLabel);
        hexRow.addView(etHex);
        content.addView(hexRow);

        // ── RGB Sliders
        String[] sliderLabels = {"R", "G", "B"};
        int[] sliderColors = {0xFFEF5350, 0xFF66BB6A, 0xFF42A5F5};
        android.widget.SeekBar[] rgbBars = new android.widget.SeekBar[3];
        android.widget.TextView[] rgbVals = new android.widget.TextView[3];

        for (int si = 0; si < 3; si++) {
            final int idx = si;

            android.widget.LinearLayout sliderRow = new android.widget.LinearLayout(this);
            sliderRow.setOrientation(android.widget.LinearLayout.HORIZONTAL);
            sliderRow.setGravity(android.view.Gravity.CENTER_VERTICAL);
            android.widget.LinearLayout.LayoutParams slrLp =
                    new android.widget.LinearLayout.LayoutParams(
                            android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
                            android.widget.LinearLayout.LayoutParams.WRAP_CONTENT);
            slrLp.setMargins(0, dpToPx(3), 0, dpToPx(3));
            sliderRow.setLayoutParams(slrLp);

            android.widget.TextView lbl = new android.widget.TextView(this);
            lbl.setText(sliderLabels[si]);
            lbl.setTextColor(sliderColors[si]);
            lbl.setTextSize(11);
            lbl.setTypeface(null, android.graphics.Typeface.BOLD);
            lbl.setLayoutParams(new android.widget.LinearLayout.LayoutParams(dpToPx(18),
                    android.widget.LinearLayout.LayoutParams.WRAP_CONTENT));

            android.widget.SeekBar sb = new android.widget.SeekBar(this);
            sb.setMax(255);
            int initVal = si == 0 ? android.graphics.Color.red(initialColor)
                        : si == 1 ? android.graphics.Color.green(initialColor)
                        : android.graphics.Color.blue(initialColor);
            sb.setProgress(initVal);
            android.widget.LinearLayout.LayoutParams sbLp =
                    new android.widget.LinearLayout.LayoutParams(0,
                            android.widget.LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
            sbLp.setMargins(dpToPx(4), 0, dpToPx(4), 0);
            sb.setLayoutParams(sbLp);
            sb.getProgressDrawable().setColorFilter(
                    sliderColors[si], android.graphics.PorterDuff.Mode.SRC_IN);
            sb.getThumb().setColorFilter(
                    sliderColors[si], android.graphics.PorterDuff.Mode.SRC_IN);

            android.widget.TextView val = new android.widget.TextView(this);
            val.setText(String.valueOf(initVal));
            val.setTextColor(0xFFCFD8DC);
            val.setTextSize(10);
            val.setLayoutParams(new android.widget.LinearLayout.LayoutParams(dpToPx(28),
                    android.widget.LinearLayout.LayoutParams.WRAP_CONTENT));
            val.setGravity(android.view.Gravity.END);

            rgbBars[idx] = sb;
            rgbVals[idx] = val;

            sliderRow.addView(lbl);
            sliderRow.addView(sb);
            sliderRow.addView(val);
            content.addView(sliderRow);
        }

        // RGB SeekBar listeners
        for (int si = 0; si < 3; si++) {
            final int idx = si;
            rgbBars[si].setOnSeekBarChangeListener(new android.widget.SeekBar.OnSeekBarChangeListener() {
                @Override public void onStartTrackingTouch(android.widget.SeekBar s) {}
                @Override public void onStopTrackingTouch(android.widget.SeekBar s) {}
                @Override public void onProgressChanged(android.widget.SeekBar s, int progress, boolean fromUser) {
                    if (!fromUser) return;
                    rgbVals[idx].setText(String.valueOf(progress));
                    int r = rgbBars[0].getProgress();
                    int g = rgbBars[1].getProgress();
                    int b2 = rgbBars[2].getProgress();
                    int newColor = android.graphics.Color.rgb(r, g, b2);
                    selectedColor[0] = newColor;
                    previewBg.setColor(newColor);
                    colorPreview.setBackground(previewBg);
                    onColorSelected.accept(newColor); // real-time apply
                }
            });
        }

        etHex.addTextChangedListener(new android.text.TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int st, int c2, int a) {}
            @Override public void onTextChanged(CharSequence s, int st, int b2, int c2) {
                try {
                    String hex = s.toString().trim();
                    if (!hex.startsWith("#")) hex = "#" + hex;
                    if (hex.length() == 7) {
                        int parsed = android.graphics.Color.parseColor(hex);
                        selectedColor[0] = parsed;
                        previewBg.setColor(parsed);
                        colorPreview.setBackground(previewBg);
                        rgbBars[0].setProgress(android.graphics.Color.red(parsed));
                        rgbBars[1].setProgress(android.graphics.Color.green(parsed));
                        rgbBars[2].setProgress(android.graphics.Color.blue(parsed));
                        rgbVals[0].setText(String.valueOf(android.graphics.Color.red(parsed)));
                        rgbVals[1].setText(String.valueOf(android.graphics.Color.green(parsed)));
                        rgbVals[2].setText(String.valueOf(android.graphics.Color.blue(parsed)));
                        onColorSelected.accept(parsed); // real-time apply
                    }
                } catch (Exception ignored) {}
            }
            @Override public void afterTextChanged(android.text.Editable s) {}
        });

        // Update hex from RGB sliders (second pass for hex sync)
        // — removed duplicate listener block —

        // ── OK / Cancel
        android.widget.LinearLayout btnRow = new android.widget.LinearLayout(this);
        btnRow.setOrientation(android.widget.LinearLayout.HORIZONTAL);
        btnRow.setLayoutParams(new android.widget.LinearLayout.LayoutParams(
                android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
                android.widget.LinearLayout.LayoutParams.WRAP_CONTENT));

        android.widget.Button btnCancel = new android.widget.Button(this);
        btnCancel.setText("Cancel");
        btnCancel.setTextColor(0xFFFFFFFF);
        btnCancel.setTextSize(12);
        btnCancel.setBackgroundColor(0xFF6B7280);
        btnCancel.setStateListAnimator(null);
        android.widget.LinearLayout.LayoutParams cancelLp =
                new android.widget.LinearLayout.LayoutParams(0,
                        android.widget.LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        cancelLp.setMargins(0, 0, dpToPx(4), 0);
        btnCancel.setLayoutParams(cancelLp);

        android.widget.Button btnOk = new android.widget.Button(this);
        btnOk.setText("✅ OK");
        btnOk.setTextColor(0xFFFFFFFF);
        btnOk.setTextSize(12);
        btnOk.setBackgroundColor(0xFF607D8B);
        btnOk.setStateListAnimator(null);
        btnOk.setLayoutParams(new android.widget.LinearLayout.LayoutParams(0,
                android.widget.LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

        btnRow.addView(btnCancel);
        btnRow.addView(btnOk);
        content.addView(btnRow);

        // ── PopupWindow
        int screenW = getResources().getDisplayMetrics().widthPixels;
        android.widget.PopupWindow popup = new android.widget.PopupWindow(
                root,
                screenW,
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
                true);
        popup.setBackgroundDrawable(new android.graphics.drawable.ColorDrawable(
                android.graphics.Color.TRANSPARENT));
        popup.setElevation(16f);
        popup.setOutsideTouchable(true);

        android.view.View rootView = getWindow().getDecorView().getRootView();
        popup.showAtLocation(rootView, android.view.Gravity.CENTER, 0, 0);

        // ── Drag logic
        final int[] lastXY = {0, 0};
        dragHandle.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case android.view.MotionEvent.ACTION_DOWN:
                    lastXY[0] = (int) event.getRawX();
                    lastXY[1] = (int) event.getRawY();
                    break;
                case android.view.MotionEvent.ACTION_MOVE:
                    int dx = (int) event.getRawX() - lastXY[0];
                    int dy = (int) event.getRawY() - lastXY[1];
                    int[] loc = new int[2];
                    root.getLocationOnScreen(loc);
                    popup.update(loc[0] + dx, loc[1] + dy, screenW, -1);
                    lastXY[0] = (int) event.getRawX();
                    lastXY[1] = (int) event.getRawY();
                    break;
            }
            return true;
        });

        btnClose.setOnClickListener(v -> {
            onColorSelected.accept(initialColor); // restore original
            popup.dismiss();
        });
        btnCancel.setOnClickListener(v -> {
            onColorSelected.accept(initialColor); // restore original
            popup.dismiss();
        });
        btnOk.setOnClickListener(v -> {
            onColorSelected.accept(selectedColor[0]);
            popup.dismiss();
        });
    }


}