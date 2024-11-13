import {FormEvent} from "react";
import axios from "axios";
import {useTranslation} from "react-i18next";
import Item from "../../../../type/Item.tsx";
import RegistryConfig from "../../../../type/RegistryConfig.tsx";

export default function ItemComponent(
    {
        config,
        item,
        removeItem
    }: {
        readonly config: RegistryConfig
        readonly item: Item,
        readonly removeItem: (item: Item) => void
    }
) {
    const {t} = useTranslation();
    const handleSubmit = function (event: FormEvent) {
        event.preventDefault();
        axios.delete(`${config.item.url}/${item.privateId}`)
            .then(() => {
                removeItem(item);
            })
            .catch(error => {
                console.error('Error fetching data:', error);
            });
    }
    return <form className="item-form" onSubmit={handleSubmit}>
        <label htmlFor="title">{t("item_name")}</label>
        <input type="text" name="title" disabled={true} value={item.product.title}/>
        <label htmlFor="description">{t("item_description")}</label>
        <input type="text" name="description" disabled={true} value={item.product.description}/>
        <label htmlFor="link">{t("item_link")}</label>
        <input type="text" name="link" disabled={true} value={item.product.link}/>
        <label htmlFor="quantity">{t("item_quantity")}</label>
        <input type="text" name="quantity" disabled={true} value={item.quantity}/>
        <button>{t("remove_item")}</button>
    </form>
}
