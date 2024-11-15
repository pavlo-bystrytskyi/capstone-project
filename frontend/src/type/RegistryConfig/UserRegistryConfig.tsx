import RegistryConfig from "../RegistryConfig.tsx";

const userRegistryConfig: RegistryConfig = {
    wishlist: {
        url: "/api/user/wishlist",
        itemIdField: "privateItemIds"
    },
    item: {
        url: "/api/user/item",
        idField: "privateId"
    },
    access: {
        wishlist: {
            edit: {
                allowed: true,
                url: "/edit-user"
            }
        },
        item: {
            status: {
                edit: {
                    allowed: false
                }
            }
        }
    }
}

export default userRegistryConfig;