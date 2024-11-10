import {FormEvent, useEffect, useState} from "react";
import axios from "axios";
import {useTranslation} from "react-i18next";
import Item from "../../../../../type/Item.tsx";
import {emptyItem} from "../../../../../type/EmptyItem.tsx";
import ItemIdContainer from "../../../../../type/ItemIdContainer.tsx";

export default function ItemComponent(
    {
        itemId,
        removeItemId
    }: {
        itemId: ItemIdContainer,
        readonly removeItemId: (itemId: ItemIdContainer) => void
    }
) {
    const {t} = useTranslation();
    const [item, setItem] = useState<Item>(emptyItem)
    const handleSubmit = function (event: FormEvent) {
        event.preventDefault();
        axios.delete('/api/item/' + itemId.privateId)
            .then(() => {
                removeItemId(itemId);
            })
            .catch(error => {
                console.error('Error fetching data:', error);
            });
    }
    const loadItem = function () {
        axios.get<Item>("/api/item/" + itemId.privateId)
            .then(result => {
                    setItem(result.data);
                }
            ).catch(error => {
            console.error('Error fetching data:', error);
        });
    }
    useEffect(loadItem, [itemId]);

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
