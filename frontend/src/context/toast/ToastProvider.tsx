import {ReactNode, useState} from 'react';
import {Toast, ToastContainer} from 'react-bootstrap';
import {ToastContext} from './ToastContext';
import ToastVariant from "./ToastVariant.tsx";

type ToastMessage = {
    id: number;
    message: string;
    variant: ToastVariant;
};

export default function ToastProvider(
    {
        children
    }: {
        children: ReactNode
    }
) {
    const [toasts, setToasts] = useState<ToastMessage[]>([]);
    const addToast = (message: string, variant: ToastVariant) => {
        const id = Date.now();
        setToasts((prev) => [...prev, {id, message, variant}]);
        setTimeout(() => {
            setToasts((prev) => prev.filter((toast) => toast.id !== id));
        }, 3000);
    };

    return (
        <ToastContext.Provider value={{addToast}}>
            {children}
            <ToastContainer position="bottom-center" className="mx-3" style={{paddingBottom: '3rem'}}>
                {toasts.map((toast) => (
                    <Toast key={toast.id} bg={toast.variant} className="mx-3 text-center">
                        <Toast.Body>{toast.message}</Toast.Body>
                    </Toast>
                ))}
            </ToastContainer>
        </ToastContext.Provider>
    );
};

