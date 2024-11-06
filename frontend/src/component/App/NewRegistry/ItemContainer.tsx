import NewItemForm from "./ItemContainer/NewItemForm.tsx";
import Item from "../../../type/Item.tsx";
import ItemComponent from "./ItemContainer/ItemComponent.tsx";
import {useTranslation} from "react-i18next";

export default function ItemContainer(
    {
        itemList,
        setItemList
    }: {
        itemList: Item[],
        setItemList: (itemList: Item[]) => void
    }
) {
    const {t} = useTranslation();
    const addItem = function (item: Item) {
        setItemList([...itemList, item]);
    }
    const removeItem = function (itemToRemove: Item) {
        setItemList(
            itemList.filter(
                (item) => itemToRemove !== item)
        );
    }

    return <>
        <h2>Item Container</h2>
        <NewItemForm addItem={addItem}/>
        {
            (Array.isArray(itemList) && itemList.length > 0) ?
                itemList.map(
                    (item) => <ItemComponent key={item.privateId} item={item} removeItem={removeItem}/>
                ) : <div>{t("add_some_products")}</div>
        }
    </>
}
