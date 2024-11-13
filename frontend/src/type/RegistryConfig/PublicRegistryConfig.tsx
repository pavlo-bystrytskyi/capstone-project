import RegistryConfig from "../RegistryConfig.tsx";

const publicRegistryConfig: RegistryConfig = {
    wishlist: {
        url: "/api/guest/wishlist/public",
        itemIdField: "publicItemIds"
    },
    item: {
        url: "/api/guest/item/public",
        idField: "publicId"
    },
    access: {
        wishlist: {
            edit: false
        },
        item: {
            status: {
                edit: true
            }
        }
    }
}

export default publicRegistryConfig;