import Registry from "./Registry.tsx";

type RegistryRestricted = Pick<Registry, "title" | "description" | "itemIds" | "active" | "deactivationDate">;

export default RegistryRestricted
