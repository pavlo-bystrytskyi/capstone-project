import RegistryConfig from "../RegistryConfig.tsx";

const privateRegistryConfig: RegistryConfig = {
    wishlist: {
        url: "/api/wishlist",
        itemIdField: "privateItemIds"
    },
    item: {
        url: "/api/item",
        idField: "privateId"
    },
    access: {
        wishlist: {
            edit: true
        },
        item: {
            status: {
                edit: false
            }
        }
    }
}

export default privateRegistryConfig;