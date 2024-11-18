import ItemStatus from "./ItemStatus.tsx";
import ItemRestricted from "./ItemRestricted.tsx";

export const emptyItem: ItemRestricted = {
    status: ItemStatus.AVAILABLE,
    quantity: 1,
    product: {
        title: "",
        description: "",
        link: ""
    }
};
