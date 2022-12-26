package org.xtx.ut4converter.tools;

import java.util.List;

/**
 * Light Json structure returned by GitHub apif for releases
 */
public class GitHubReleaseJson {

    /**
     * Name of release.
     * E.g: v1.1.1
     */
    private String tagName;

    /**
     * Url of the release
     */
    private String htmlUrl;

    /**
     * List of available release packages
     */
    private List<Asset> assets;

    static class Asset {

        /**
         * Size in bytes of the release package
         * E.g: 89117095
         */
        private int size;

        /**
         * Download url of the release package
         */
        private String browseDownloadUrl;

        public int getSize() {
            return size;
        }

        public void setSize(int size) {
            this.size = size;
        }

        public String getBrowseDownloadUrl() {
            return browseDownloadUrl;
        }

        public void setBrowseDownloadUrl(String browserDownloadUrl) {
            this.browseDownloadUrl = browserDownloadUrl;
        }
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public List<Asset> getAssets() {
        return assets;
    }

    public void setAssets(List<Asset> assets) {
        this.assets = assets;
    }

    public String getHtmlUrl() {
        return htmlUrl;
    }

    public void setHtmlUrl(String htmlUrl) {
        this.htmlUrl = htmlUrl;
    }
}
