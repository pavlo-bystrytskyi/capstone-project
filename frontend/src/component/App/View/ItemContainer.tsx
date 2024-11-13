import ItemComponent from "./ItemContainer/ItemComponent.tsx";
import RegistryConfig from "../../../type/RegistryConfig.tsx";
import ItemIdContainer from "../../../type/ItemIdContainer.tsx";

export default function ItemContainer(
    {
        itemIdList,
        config
    }: {
        readonly itemIdList: ItemIdContainer[],
        readonly config: RegistryConfig
    }
) {
    return <>
        <h2>Item Container</h2>
        {
            itemIdList.map(
                (itemId) => <ItemComponent key={itemId.publicId} itemIdContainer={itemId} config={config}/>
            )
        }
    </>
}
