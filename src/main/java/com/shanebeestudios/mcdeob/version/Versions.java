package com.shanebeestudios.mcdeob.version;

import com.shanebeestudios.mcdeob.util.Util;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Versions {

    private static final Map<String, Version> VERSION_MAP = new LinkedHashMap<>();
    private static final Map<String, Version> RELEASE_MAP = new LinkedHashMap<>();
    private static final Map<String, Version> SNAPSHOT_MAP = new LinkedHashMap<>();

    public static void initVersions() {
        JSONObject mojangManifest = Util.getJsonFromURL("https://launchermeta.mojang.com/mc/game/version_manifest.json");
        JSONObject seargeManifest = Util.getJsonFromURL("https://raw.githubusercontent.com/ShaneBeeStudios/Mappings/refs/heads/main/mappings/versions.json");

        boolean searge = false;
        List<String> seargeVersions = new ArrayList<>();
        for (Object o : seargeManifest.getJSONArray("versions")) {
            seargeVersions.add(o.toString());
        }

        for (Object o : mojangManifest.getJSONArray("versions")) {
            JSONObject versionObject = (JSONObject) o;
            String id = versionObject.getString("id");
            String type = versionObject.getString("type");
            String url = versionObject.getString("url");

            if (searge && !seargeVersions.contains(id)) {
                continue;
            }

            Version.ReleaseType releaseType = type.equalsIgnoreCase("release") ? Version.ReleaseType.RELEASE : Version.ReleaseType.SNAPSHOT;
            Version version = new Version(id, releaseType, url, searge);
            VERSION_MAP.put(id, version);
            if (releaseType == Version.ReleaseType.RELEASE) {
                RELEASE_MAP.put(id, version);
            } else {
                SNAPSHOT_MAP.put(id, version);
            }

            // Mojang mappings not available before 1.14.4
            // so we use searge mappings
            if (id.equalsIgnoreCase("1.14.4")) {
                searge = true;
            }
        }
    }

    public static Collection<Version> getAllVersions() {
        return VERSION_MAP.values();
    }

    public static Collection<Version> getReleaseVersions() {
        return RELEASE_MAP.values();
    }

    public static Collection<Version> getSnapshotVersions() {
        return SNAPSHOT_MAP.values();
    }

    @Nullable
    public static Version getByVersion(String version) {
        return VERSION_MAP.get(version);
    }

}
