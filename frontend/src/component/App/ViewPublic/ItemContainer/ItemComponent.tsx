import Item from "../../../../type/Item.tsx";
import {useTranslation} from "react-i18next";

export default function ItemComponent(
    {
        item
    }: {
        item: Item
    }
) {
    const {t} = useTranslation();

    return <form className="item-form">
        <label htmlFor="title">{t("item_name")}</label>
        <input type="text" name="title" disabled={true} value={item.product.title}/>
        <label htmlFor="description">{t("item_description")}</label>
        <input type="text" name="description" disabled={true} value={item.product.description}/>
        <label htmlFor="link">{t("item_link")}</label>
        <input type="text" name="link" disabled={true} value={item.product.link}/>
        <label htmlFor="quantity">{t("item_quantity")}</label>
        <input type="text" name="quantity" disabled={true} value={item.quantity}/>
    </form>
}
