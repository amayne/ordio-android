package com.ordio.player;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Log;

public class AlbumArtUtils {
	/*
	private static final String UNKNOWN_STRING = "<unknown>";
	
	static Activity act = null;
	
	public AlbumArtUtils(LibraryTabActivity activity) {
		act = activity;
	}
	
	// A really simple BitmapDrawable-like class, that doesn't do
    // scaling, dithering or filtering.
    private static class FastBitmapDrawable extends Drawable {
        private Bitmap mBitmap;
        public FastBitmapDrawable(Bitmap b) {
            mBitmap = b;
        }
        @Override
        public void draw(Canvas canvas) {
            canvas.drawBitmap(mBitmap, 0, 0, null);
        }
        @Override
        public int getOpacity() {
            return PixelFormat.OPAQUE;
        }
        @Override
        public void setAlpha(int alpha) {
        }
        @Override
        public void setColorFilter(ColorFilter cf) {
        }
    }
    
	private static int sArtId = -2;
    private static byte [] mCachedArt;
    private static Bitmap mCachedBit = null;
    private static final BitmapFactory.Options sBitmapOptionsCache = new BitmapFactory.Options();
    private static final BitmapFactory.Options sBitmapOptions = new BitmapFactory.Options();
    private static final Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");
    private static final HashMap<Integer, Drawable> sArtCache = new HashMap<Integer, Drawable>();
    private static int sArtCacheId = -1;

    static {
        // for the cache,
        // 565 is faster to decode and display
        // and we don't want to dither here because the image will be scaled down later
        sBitmapOptionsCache.inPreferredConfig = Bitmap.Config.RGB_565;
        sBitmapOptionsCache.inDither = false;

        sBitmapOptions.inPreferredConfig = Bitmap.Config.RGB_565;
        sBitmapOptions.inDither = false;
    }

    public static void initAlbumArtCache() {
        try {
            int id = sService.getMediaMountedCount();
            if (id != sArtCacheId) {
                clearAlbumArtCache();
                sArtCacheId = id;
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public static void clearAlbumArtCache() {
        synchronized(sArtCache) {
            sArtCache.clear();
        }
    }

    public static Drawable getCachedArtwork(Context context, int artIndex, BitmapDrawable defaultArtwork) {
        Drawable d = null;
        synchronized(sArtCache) {
            d = sArtCache.get(artIndex);
        }
        if (d == null) {
            d = defaultArtwork;
            final Bitmap icon = defaultArtwork.getBitmap();
            int w = icon.getWidth();
            int h = icon.getHeight();
            Bitmap b = AlbumArtUtils.getArtworkQuick(context, artIndex, w, h);
            if (b != null) {
                d = new FastBitmapDrawable(b);
                synchronized(sArtCache) {
                    // the cache may have changed since we checked
                    Drawable value = sArtCache.get(artIndex);
                    if (value == null) {
                        sArtCache.put(artIndex, d);
                    } else {
                        d = value;
                    }
                }
            }
        }
        return d;
    }

    // Get album art for specified album. This method will not try to
    // fall back to getting artwork directly from the file, nor will
    // it attempt to repair the database.
    private static Bitmap getArtworkQuick(Context context, int album_id, int w, int h) {
        // NOTE: There is in fact a 1 pixel border on the right side in the ImageView
        // used to display this drawable. Take it into account now, so we don't have to
        // scale later.
        w -= 1;
        ContentResolver res = context.getContentResolver();
        Uri uri = ContentUris.withAppendedId(sArtworkUri, album_id);
        if (uri != null) {
            ParcelFileDescriptor fd = null;
            try {
                fd = res.openFileDescriptor(uri, "r");
                int sampleSize = 1;

                // Compute the closest power-of-two scale factor
                // and pass that to sBitmapOptionsCache.inSampleSize, which will
                // result in faster decoding and better quality
                sBitmapOptionsCache.inJustDecodeBounds = true;
                BitmapFactory.decodeFileDescriptor(
                        fd.getFileDescriptor(), null, sBitmapOptionsCache);
                int nextWidth = sBitmapOptionsCache.outWidth >> 1;
                int nextHeight = sBitmapOptionsCache.outHeight >> 1;
                while (nextWidth>w && nextHeight>h) {
                    sampleSize <<= 1;
                    nextWidth >>= 1;
                    nextHeight >>= 1;
                }

                sBitmapOptionsCache.inSampleSize = sampleSize;
                sBitmapOptionsCache.inJustDecodeBounds = false;
                Bitmap b = BitmapFactory.decodeFileDescriptor(
                        fd.getFileDescriptor(), null, sBitmapOptionsCache);

                if (b != null) {
                    // finally rescale to exactly the size we need
                    if (sBitmapOptionsCache.outWidth != w || sBitmapOptionsCache.outHeight != h) {
                        Bitmap tmp = Bitmap.createScaledBitmap(b, w, h, true);
                        // Bitmap.createScaledBitmap() can return the same bitmap
                        if (tmp != b) b.recycle();
                        b = tmp;
                    }
                }

                return b;
            } catch (FileNotFoundException e) {
            } finally {
                try {
                    if (fd != null)
                        fd.close();
                } catch (IOException e) {
                }
            }
        }
        return null;
    }

    /** Get album art for specified album. You should not pass in the album id
     * for the "unknown" album here (use -1 instead)
     */
//    public static Bitmap getArtwork(Context context, int album_id) {
//        return getArtwork(context, album_id, true);
//    }

    /** Get album art for specified album. You should not pass in the album id
     * for the "unknown" album here (use -1 instead)
     */
/*
    public static Bitmap getArtwork(Context context, int album_id, boolean allowDefault) {

        if (album_id < 0) {
            // This is something that is not in the database, so get the album art directly
            // from the file.
            Bitmap bm = getArtworkFromFile(context, null, -1);
            if (bm != null) {
                return bm;
            }
            if (allowDefault) {
                return getDefaultArtwork(context);
            } else {
                return null;
            }
        }

        ContentResolver res = context.getContentResolver();
        Uri uri = ContentUris.withAppendedId(sArtworkUri, album_id);
        if (uri != null) {
            InputStream in = null;
            try {
                in = res.openInputStream(uri);
                return BitmapFactory.decodeStream(in, null, sBitmapOptions);
            } catch (FileNotFoundException ex) {
                // The album art thumbnail does not actually exist. Maybe the user deleted it, or
                // maybe it never existed to begin with.
                Bitmap bm = getArtworkFromFile(context, null, album_id);
                if (bm != null) {
                    // Put the newly found artwork in the database.
                    // Note that this shouldn't be done for the "unknown" album,
                    // but if this method is called correctly, that won't happen.

                    // first write it somewhere
                    String file = Environment.getExternalStorageDirectory()
                        + "/albumthumbs/" + String.valueOf(System.currentTimeMillis());
                    if (ensureFileExists(file)) {
                        try {
                            OutputStream outstream = new FileOutputStream(file);
                            if (bm.getConfig() == null) {
                                bm = bm.copy(Bitmap.Config.RGB_565, false);
                                if (bm == null) {
                                    if (allowDefault) {
                                        return getDefaultArtwork(context);
                                    } else {
                                        return null;
                                    }
                                }
                            }
                            boolean success = bm.compress(Bitmap.CompressFormat.JPEG, 75, outstream);
                            outstream.close();
                            if (success) {
                                ContentValues values = new ContentValues();
                                values.put("album_id", album_id);
                                values.put("_data", file);
                                Uri newuri = res.insert(sArtworkUri, values);
                                if (newuri == null) {
                                    // Failed to insert in to the database. The most likely
                                    // cause of this is that the item already existed in the
                                    // database, and the most likely cause of that is that
                                    // the album was scanned before, but the user deleted the
                                    // album art from the sd card.
                                    // We can ignore that case here, since the media provider
                                    // will regenerate the album art for those entries when
                                    // it detects this.
                                    success = false;
                                }
                            }
                            if (!success) {
                                File f = new File(file);
                                f.delete();
                            }
                        } catch (FileNotFoundException e) {
                            Log.e("Error", "error creating file", e);
                        } catch (IOException e) {
                            Log.e("Error", "error creating file", e);
                        }
                    }
                } else if (allowDefault) {
                    bm = getDefaultArtwork(context);
                } else {
                    bm = null;
                }
                return bm;
            } finally {
                try {
                    if (in != null) {
                        in.close();
                    }
                } catch (IOException ex) {
                }
            }
        }

        return null;
    }

    // copied from MediaProvider
    private static boolean ensureFileExists(String path) {
        File file = new File(path);
        if (file.exists()) {
            return true;
        } else {
            // we will not attempt to create the first directory in the path
            // (for example, do not create /sdcard if the SD card is not mounted)
            int secondSlash = path.indexOf('/', 1);
            if (secondSlash < 1) return false;
            String directoryPath = path.substring(0, secondSlash);
            File directory = new File(directoryPath);
            if (!directory.exists())
                return false;
            file.getParentFile().mkdirs();
            try {
                return file.createNewFile();
            } catch(IOException ioe) {
                Log.d("Error", "File creation failed for " + path);
            }
            return false;
        }
    }

    // get album art for specified file
    private static final String sExternalMediaUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI.toString();
    private static Bitmap getArtworkFromFile(Context context, Uri uri, int albumid) {
        Bitmap bm = null;
        byte [] art = null;
        String path = null;

        if (sArtId == albumid) {
            //Log.i("@@@@@@ ", "reusing cached data", new Exception());
            if (mCachedBit != null) {
                return mCachedBit;
            }
            art = mCachedArt;
        } else {
            // try reading embedded artwork
            if (uri == null) {
                try {
                    int curalbum = sService.getAlbumId();
                    if (curalbum == albumid || albumid < 0) {
                        path = sService.getPath();
                        if (path != null) {
                            uri = Uri.parse(path);
                        }
                    }
                } catch (RemoteException ex) {
                    return null;
                } catch (NullPointerException ex) {
                    return null;
                }
            }
            if (uri == null) {
                if (albumid >= 0) {
                    Cursor c = act.managedQuery(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                            new String[] { MediaStore.Audio.Media._ID, MediaStore.Audio.Media.ALBUM },
                            MediaStore.Audio.Media.ALBUM_ID + "=?", new String [] {String.valueOf(albumid)}, null);
                    if (c != null) {
                        c.moveToFirst();
                        if (!c.isAfterLast()) {
                            int trackid = c.getInt(0);
                            uri = ContentUris.withAppendedId(
                                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, trackid);
                        }
                        if (c.getString(1).equals(UNKNOWN_STRING)) {
                            albumid = -1;
                        }
                        c.close();
                    }
                }
            }
            if (uri != null) {
                MediaScanner scanner = new MediaScanner(context);
                ParcelFileDescriptor pfd = null;
                try {
                    pfd = context.getContentResolver().openFileDescriptor(uri, "r");
                    if (pfd != null) {
                        FileDescriptor fd = pfd.getFileDescriptor();
                        art = scanner.extractAlbumArt(fd);
                    }
                } catch (IOException ex) {
                } catch (SecurityException ex) {
                } finally {
                    try {
                        if (pfd != null) {
                            pfd.close();
                        }
                    } catch (IOException ex) {
                    }
                }
            }
        }
        // if no embedded art exists, look for AlbumArt.jpg in same directory as the media file
        if (art == null && path != null) {
            if (path.startsWith(sExternalMediaUri)) {
                // get the real path
                Cursor c = act.managedQuery(Uri.parse(path),
                        new String[] { MediaStore.Audio.Media.DATA},
                        null, null, null);
                if (c != null) {
                    c.moveToFirst();
                    if (!c.isAfterLast()) {
                        path = c.getString(0);
                    }
                    c.close();
                }
            }
            int lastSlash = path.lastIndexOf('/');
            if (lastSlash > 0) {
                String artPath = path.substring(0, lastSlash + 1) + "AlbumArt.jpg";
                File file = new File(artPath);
                if (file.exists()) {
                    art = new byte[(int)file.length()];
                    FileInputStream stream = null;
                    try {
                        stream = new FileInputStream(file);
                        stream.read(art);
                    } catch (IOException ex) {
                        art = null;
                    } finally {
                        try {
                            if (stream != null) {
                                stream.close();
                            }
                        } catch (IOException ex) {
                        }
                    }
                } else {
                    // TODO: try getting album art from the web
                }
            }
        }

        if (art != null) {
            try {
                // get the size of the bitmap
                BitmapFactory.Options opts = new BitmapFactory.Options();
                opts.inJustDecodeBounds = true;
                opts.inSampleSize = 1;
                BitmapFactory.decodeByteArray(art, 0, art.length, opts);

                // request a reasonably sized output image
                // TODO: don't hardcode the size
                while (opts.outHeight > 320 || opts.outWidth > 320) {
                    opts.outHeight /= 2;
                    opts.outWidth /= 2;
                    opts.inSampleSize *= 2;
                }

                // get the image for real now
                opts.inJustDecodeBounds = false;
                bm = BitmapFactory.decodeByteArray(art, 0, art.length, opts);
                if (albumid != -1) {
                    sArtId = albumid;
                }
                mCachedArt = art;
                mCachedBit = bm;
            } catch (Exception e) {
            }
        }
        return bm;
    }

    private static Bitmap getDefaultArtwork(Context context) {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inPreferredConfig = Bitmap.Config.ARGB_8888;
        return BitmapFactory.decodeStream(
                context.getResources().openRawResource(R.drawable.albumart_mp_unknown), null, opts);
    }
    */
}
