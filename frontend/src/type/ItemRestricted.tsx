import Item from "./Item.tsx";

type ItemRestricted = Pick<Item, "product" | "quantity" | "status">;

export default ItemRestricted
