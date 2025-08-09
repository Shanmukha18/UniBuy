import { Link } from 'react-router-dom';
import { 
  EnvelopeIcon,
  CodeBracketIcon,
  UserIcon
} from '@heroicons/react/24/outline';

const Footer = () => {
  const currentYear = new Date().getFullYear();

  return (
    <footer className="bg-gray-900 text-white">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12">
        <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
          {/* Brand Section */}
          <div className="space-y-4">
            <h3 className="text-2xl font-bold text-indigo-400">UniBuy</h3>
            <p className="text-gray-300">
              Your one-stop destination for all your shopping needs. Quality products, great prices, and excellent service.
            </p>
          </div>

          {/* Quick Links */}
          <div className="space-y-4">
            <h4 className="text-lg font-semibold text-indigo-400">Quick Links</h4>
            <ul className="space-y-2">
              <li>
                <Link to="/" className="text-gray-300 hover:text-indigo-400 transition-colors">
                  Home
                </Link>
              </li>
              <li>
                <Link to="/products" className="text-gray-300 hover:text-indigo-400 transition-colors">
                  Products
                </Link>
              </li>
              <li>
                <Link to="/orders" className="text-gray-300 hover:text-indigo-400 transition-colors">
                  Orders
                </Link>
              </li>
            </ul>
          </div>

          {/* Contact Section */}
          <div className="space-y-4">
            <h4 className="text-lg font-semibold text-indigo-400">Contact Us</h4>
            <div className="space-y-3">
              <a 
                href="mailto:shanmukha.thadavarthi@gmail.com" 
                className="flex items-center space-x-2 text-gray-300 hover:text-indigo-400 transition-colors"
                target="_blank"
                rel="noopener noreferrer"
              >
                <EnvelopeIcon className="h-5 w-5" />
                <span>shanmukha.thadavarthi@gmail.com</span>
              </a>
              <a 
                href="https://linkedin.com/in/shanmukha-thadavarthi" 
                className="flex items-center space-x-2 text-gray-300 hover:text-indigo-400 transition-colors"
                target="_blank"
                rel="noopener noreferrer"
              >
                <UserIcon className="h-5 w-5" />
                <span>LinkedIn</span>
              </a>
              <a 
                href="https://github.com/shanmukha-thadavarthi" 
                className="flex items-center space-x-2 text-gray-300 hover:text-indigo-400 transition-colors"
                target="_blank"
                rel="noopener noreferrer"
              >
                <CodeBracketIcon className="h-5 w-5" />
                <span>GitHub</span>
              </a>
            </div>
          </div>
        </div>

        {/* Copyright */}
        <div className="border-t border-gray-700 mt-8 pt-8 text-center">
          <p className="text-gray-300">
            © {currentYear} UniBuy. All rights reserved.
          </p>
        </div>
      </div>
    </footer>
  );
};

export default Footer;
