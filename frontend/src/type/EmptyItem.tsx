import ItemStatus from "./ItemStatus.tsx";
import Item from "./Item.tsx";

export const emptyItem: Item = {
    publicId: "",
    privateId: "",
    status: ItemStatus.AVAILABLE,
    quantity: 1,
    product: {
        title: "",
        description: "",
        link: ""
    }
};
