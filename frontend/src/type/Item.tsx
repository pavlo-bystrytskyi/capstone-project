import Product from "./Product.tsx";
import ItemStatus from "./ItemStatus.tsx";

type Item = {
    publicId: string,
    privateId: string
    quantity: number,
    status: ItemStatus,
    product: Product
}

export default Item