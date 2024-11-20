import ToastVariant from "./ToastVariant.tsx";

export default interface ToastContextType {
    addToast: (message: string, variant: ToastVariant) => void;
}
