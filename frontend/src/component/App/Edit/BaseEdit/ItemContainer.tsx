import NewItemForm from "./ItemContainer/NewItemForm.tsx";
import ItemComponent from "./ItemContainer/ItemComponent.tsx";
import {useTranslation} from "react-i18next";
import ItemIdContainer from "../../../../type/ItemIdContainer.tsx";

export default function ItemContainer(
    {
        itemIdList,
        setItemIdList
    }: {
        itemIdList: ItemIdContainer[],
        setItemIdList: (itemIdList: ItemIdContainer[]) => void
    }
) {
    const {t} = useTranslation();
    const addItemId = function (itemId: ItemIdContainer) {
        setItemIdList([...itemIdList, itemId]);
    }
    const removeItemId = function (itemIdToRemove: ItemIdContainer) {
        setItemIdList(
            itemIdList.filter(
                (itemId) => itemIdToRemove !== itemId)
        );
    }

    return <>
        <h2>Item Container</h2>
        <NewItemForm addItemId={addItemId}/>
        {
            (itemIdList.length > 0) ?
                itemIdList.map(
                    (itemId) => <ItemComponent key={itemId.privateId} itemId={itemId} removeItemId={removeItemId}/>
                ) : <div>{t("add_some_products")}</div>
        }
    </>
}
