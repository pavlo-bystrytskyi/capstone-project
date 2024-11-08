import Item from "../../../../type/Item.tsx";
import {useTranslation} from "react-i18next";
import ItemStatus from "../../../../type/ItemStatus.tsx";
import ViewConfig from "../../../../type/ViewConfig.tsx";
import {ChangeEvent, useEffect, useState} from "react";
import axios from "axios";

export default function ItemComponent(
    {
        itemId,
        config
    }: {
        itemId: string,
        config: ViewConfig
    }
) {
    const {t} = useTranslation();
    const editStatusAllowed = config.access.item.status.edit;
    const [item, setItem] = useState<Item>()
    const loadItem = function () {
        axios.get<Item>(
            config.item.url + itemId
        ).then(
            result => setItem(result.data)
        ).catch(
            error => console.error('Error fetching data:', error)
        );
    }
    useEffect(
        loadItem, [itemId]
    );
    const handleStatusChange = (event: ChangeEvent<HTMLSelectElement>) => {
        if (!item) return;
        const {value} = event.target;
        item.status = value as ItemStatus;
        axios.put<Item>(config.item.url + item[config.item.idField as keyof Item], item).then(
            (response) => setItem(response.data)
        ).catch(error => {
            console.error('Error fetching data:', error);
        });
    };

    return <form className="item-form">
        <label htmlFor="title">{t("item_name")}</label>
        <input type="text" name="title" disabled={true} value={item?.product.title}/>
        <label htmlFor="description">{t("item_description")}</label>
        <input type="text" name="description" disabled={true} value={item?.product.description}/>
        <label htmlFor="link">{t("item_link")}</label>
        <input type="text" name="link" disabled={true} value={item?.product.link}/>
        <label htmlFor="status">{t("item_status")}</label>
        <select name="status" disabled={!editStatusAllowed} onChange={handleStatusChange}
                value={item?.status}>
            <option value={ItemStatus.AVAILABLE}>{t("status_available")}</option>
            <option value={ItemStatus.RESERVED}>{t("status_reserved")}</option>
            <option value={ItemStatus.PURCHASED}>{t("status_purchased")}</option>
        </select>
        <label htmlFor="quantity">{t("item_quantity")}</label>
        <input type="text" name="quantity" disabled={true} value={item?.quantity}/>
    </form>
}
