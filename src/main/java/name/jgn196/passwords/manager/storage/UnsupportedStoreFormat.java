package name.jgn196.passwords.manager.storage;

class UnsupportedStoreFormat extends RuntimeException {

    UnsupportedStoreFormat(final int version) {

        super("Expected store format version " + StoreFormat.VERSION + " but got " + version);
    }
}
