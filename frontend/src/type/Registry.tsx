import ItemIdContainer from "./ItemIdContainer.tsx";

type Registry = {
    privateId: string,
    publicId: string,
    title: string,
    description: string,
    itemIds: ItemIdContainer[],
}

export default Registry