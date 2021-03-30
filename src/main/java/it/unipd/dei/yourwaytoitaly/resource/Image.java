package it.unipd.dei.yourwaytoitaly.resource;

/**
 * Class to define the objects which represents the Image in the DB
 * @author Vittorio Esposito
 * @version 1.0
 * @since 1.0
 */

public final class Image {
    private final int idImage;
    private final String path;
    private final String description;
    private final int idAdvertisement;

    public Image(final int idImage, final String path, final String description, final int idAdvertisement) {
        this.idImage = idImage;
        this.path = path;
        this.description = description;
        this.idAdvertisement = idAdvertisement;
    }
    public final int getIdImage() {
        return idImage;
    }
    public final String getPath() {
        return path;
    }
    public final int getIdAdvertisement() {
        return idAdvertisement;
    }
    public final String getDescription() {
        return description;
    }
}